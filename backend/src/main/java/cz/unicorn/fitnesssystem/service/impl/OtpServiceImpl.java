package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.OtpCheck;
import cz.unicorn.fitnesssystem.dto.OtpIssued;
import cz.unicorn.fitnesssystem.entity.OneTimeCode;
import cz.unicorn.fitnesssystem.entity.OtpPurpose;
import cz.unicorn.fitnesssystem.entity.Trainer;
import cz.unicorn.fitnesssystem.helper.HashHelper;
import cz.unicorn.fitnesssystem.helper.RandomCodeHelper;
import cz.unicorn.fitnesssystem.repository.OneTimeCodeRepository;
import cz.unicorn.fitnesssystem.service.OtpService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int CODE_DIGITS = 6;

    private final OneTimeCodeRepository codeRepository;
    private final ApplicationEventPublisher events;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void issueTrainerLogin(Trainer trainer) {
        Instant now = clock.instant();
        String email = trainer.getEmail();
        for (OneTimeCode older : codeRepository.findAllByEmailAndPurposeAndConsumedAtIsNull(email, OtpPurpose.TRAINER_LOGIN)) {
            older.setConsumedAt(now);
        }

        String code = RandomCodeHelper.digits(CODE_DIGITS);
        codeRepository.save(new OneTimeCode(OtpPurpose.TRAINER_LOGIN, email, hash(code), trainer, null,
                now, now.plus(properties.otp().ttl())));
        events.publishEvent(new OtpIssued(email, code, OtpPurpose.TRAINER_LOGIN));
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public OtpCheck check(String email, OtpPurpose purpose, String code) {
        OneTimeCode otp = codeRepository
                .findFirstWithLockByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(email, purpose)
                .orElse(null);
        if (otp == null) {
            return OtpCheck.INVALID;
        }
        Instant now = clock.instant();
        int maxAttempts = properties.otp().maxAttempts();
        if (!otp.getExpiresAt().isAfter(now)) {
            return OtpCheck.EXPIRED;
        }
        if (otp.getAttempts() >= maxAttempts) {
            return OtpCheck.LOCKED;
        }
        if (MessageDigest.isEqual(hash(code), otp.getCodeHash())) {
            otp.setConsumedAt(now);
            return OtpCheck.VALID;
        }
        otp.setAttempts((short) (otp.getAttempts() + 1));
        return otp.getAttempts() >= maxAttempts ? OtpCheck.LOCKED : OtpCheck.INVALID;
    }

    private byte[] hash(String code) {
        return HashHelper.sha256((code + properties.otp().pepper()).getBytes(StandardCharsets.UTF_8));
    }
}