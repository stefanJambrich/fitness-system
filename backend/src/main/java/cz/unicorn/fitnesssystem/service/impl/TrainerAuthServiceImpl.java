package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.LoginResult;
import cz.unicorn.fitnesssystem.dto.OtpCheck;
import cz.unicorn.fitnesssystem.entity.OtpPurpose;
import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.helper.RateLimitHelper;
import cz.unicorn.fitnesssystem.repository.TrainerRepository;
import cz.unicorn.fitnesssystem.service.OtpService;
import cz.unicorn.fitnesssystem.service.TrainerAuthService;
import cz.unicorn.fitnesssystem.service.TrainerSessionService;
import java.time.Clock;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class TrainerAuthServiceImpl implements TrainerAuthService {

    private final TrainerRepository trainerRepository;
    private final OtpService otpService;
    private final TrainerSessionService sessionService;
    private final RateLimitHelper rateLimitHelper;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void register(String email, String displayName, String clientIp) {
        String normalized = normalizeEmail(email);
        String name = displayName.strip();
        if (name.isEmpty()) {
            throw ApiException.validation("displayName", "must not be blank");
        }
        limitCodeRequests(normalized, clientIp);

        trainerRepository.lockEmail(normalized);
        Trainer trainer = trainerRepository.findByEmail(normalized)
                .orElseGet(() -> trainerRepository.save(new Trainer(normalized, name, clock.instant())));
        if (trainer.getDeactivatedAt() == null) {
            otpService.issueTrainerLogin(trainer);
        }
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void requestLoginCode(String email, String clientIp) {
        String normalized = normalizeEmail(email);
        limitCodeRequests(normalized, clientIp);

        trainerRepository.lockEmail(normalized);
        trainerRepository.findByEmail(normalized)
                .filter(trainer -> trainer.getDeactivatedAt() == null)
                .ifPresent(otpService::issueTrainerLogin);
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public LoginResult verifyLogin(String email, String code, boolean remember, String userAgent) {
        String normalized = normalizeEmail(email);
        OtpCheck check = otpService.check(normalized, OtpPurpose.TRAINER_LOGIN, code);
        if (check != OtpCheck.VALID) {
            return rejected(check);
        }
        Optional<Trainer> active = trainerRepository.findByEmail(normalized)
                .filter(trainer -> trainer.getDeactivatedAt() == null);
        if (active.isEmpty()) {
            return rejected(OtpCheck.INVALID);
        }

        Trainer trainer = active.get();
        if (trainer.getEmailVerifiedAt() == null) {
            trainer.setEmailVerifiedAt(clock.instant());
        }
        String token = sessionService.open(trainer, remember, userAgent);
        return new LoginResult.Success(trainer, token, remember);
    }

    /** {@inheritDoc} */
    @Override
    public Trainer getTrainer(UUID trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED", "Log in again"));
    }

    private void limitCodeRequests(String email, String clientIp) {
        AppProperties.Otp limits = properties.otp();
        rateLimitHelper.acquire("otp-ip:" + clientIp, limits.limitPerIp(), limits.ipWindow());
        rateLimitHelper.acquire("otp-email:" + email, limits.limitPerEmail(), limits.emailWindow());
    }

    private static LoginResult rejected(OtpCheck check) {
        ApiException error = switch (check) {
            case EXPIRED -> unauthorized("OTP_EXPIRED", "The code has expired, request a new one");
            case LOCKED -> unauthorized("OTP_LOCKED", "Too many wrong attempts, request a new code");
            default -> unauthorized("OTP_INVALID", "The code is not valid");
        };
        return new LoginResult.Rejected(error);
    }

    private static ApiException unauthorized(String code, String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, code, message);
    }

    private static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}