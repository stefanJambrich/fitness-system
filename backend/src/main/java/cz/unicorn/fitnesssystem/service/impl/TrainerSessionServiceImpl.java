package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.auth.Base64Url;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.TrainerPrincipal;
import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.entity.TrainerSession;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.helper.HashHelper;
import cz.unicorn.fitnesssystem.helper.RandomCodeHelper;
import cz.unicorn.fitnesssystem.repository.TrainerSessionRepository;
import cz.unicorn.fitnesssystem.service.TrainerSessionService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class TrainerSessionServiceImpl implements TrainerSessionService {

    private static final int TOKEN_BYTES = 32;

    private final TrainerSessionRepository sessionRepository;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public String open(Trainer trainer, boolean remembered, String userAgent) {
        byte[] token = RandomCodeHelper.token(TOKEN_BYTES);
        AppProperties.TrainerSession config = properties.trainerSession();
        Instant now = clock.instant();
        Instant expiresAt = now.plus(remembered ? config.rememberedLifetime() : config.lifetime());

        sessionRepository.save(new TrainerSession(trainer, HashHelper.sha256(token), remembered,
                deviceLabel(userAgent), now, expiresAt));
        return Base64Url.encode(token);
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public TrainerPrincipal authenticate(String token) {
        Instant now = clock.instant();
        TrainerSession session = tokenHash(token)
                .flatMap(sessionRepository::findWithTrainerByTokenHash)
                .filter(s -> s.getRevokedAt() == null
                        && s.getExpiresAt().isAfter(now)
                        && s.getTrainer().getDeactivatedAt() == null)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED",
                        "Your session has expired, log in again"));

        AppProperties.TrainerSession config = properties.trainerSession();
        if (Duration.between(session.getLastUsedAt(), now).compareTo(config.lastUsedInterval()) >= 0) {
            session.setLastUsedAt(now);
            if (session.isRemembered()) {
                session.setExpiresAt(now.plus(config.rememberedLifetime()));
            }
        }
        return new TrainerPrincipal(session.getTrainer().getId(), session.getId(), session.isRemembered());
    }

    /** {@inheritDoc} */
    @Override
    public List<TrainerSession> listActive(UUID trainerId) {
        return sessionRepository.findAllByTrainerIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtDesc(
                trainerId, clock.instant());
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void revoke(UUID trainerId, UUID sessionId) {
        Instant now = clock.instant();
        sessionRepository.findByIdAndTrainerIdAndRevokedAtIsNullAndExpiresAtAfter(sessionId, trainerId, now)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Session not found"))
                .setRevokedAt(now);
    }

    private static Optional<byte[]> tokenHash(String token) {
        try {
            byte[] bytes = Base64Url.decode(token);
            return bytes.length == TOKEN_BYTES ? Optional.of(HashHelper.sha256(bytes)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static String deviceLabel(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        String browser = userAgent.contains("Edg/") ? "Edge"
                : userAgent.contains("Firefox/") ? "Firefox"
                : userAgent.contains("Chrome/") ? "Chrome"
                : userAgent.contains("Safari/") ? "Safari"
                : "Browser";
        String os = userAgent.contains("Android") ? "Android"
                : userAgent.contains("iPhone") || userAgent.contains("iPad") ? "iOS"
                : userAgent.contains("Windows") ? "Windows"
                : userAgent.contains("Mac OS X") ? "macOS"
                : userAgent.contains("Linux") ? "Linux"
                : null;
        return os == null ? browser : browser + " on " + os;
    }
}