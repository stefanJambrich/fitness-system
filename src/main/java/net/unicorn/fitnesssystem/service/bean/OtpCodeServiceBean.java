package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.annotations.ReadWriteTransaction;
import net.unicorn.fitnesssystem.entity.OtpCode;
import net.unicorn.fitnesssystem.exceptions.OtpVerificationException;
import net.unicorn.fitnesssystem.repository.OtpCodeRepository;
import net.unicorn.fitnesssystem.service.EmailSenderService;
import net.unicorn.fitnesssystem.service.OtpCodeService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Random;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class OtpCodeServiceBean implements OtpCodeService {

    private final OtpCodeRepository otpCodeRepository;
    private final EmailSenderService emailSenderService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int EXPIRATION_MINUTES = 10;

    @Override
    @ReadWriteTransaction
    public void generateAndSendOtp(String email) {
        log.info("Generating OTP for email: {}", email);

        String plainOtp = generatePlainOtp();
        String hashedOtp = passwordEncoder.encode(plainOtp);

        log.info("CODE: {}", plainOtp);

        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        OtpCode otpCode = new OtpCode();
        otpCode.setEmail(email);
        otpCode.setCodeHash(hashedOtp);
        otpCode.setExpiresAt(expiresAt);
        otpCode.setIsUsed(false);

        otpCodeRepository.save(otpCode);
        log.info("OTP saved to database with expiration: {}", expiresAt);

        //TODO: After implementation this is probably gonna be throwing excpetions,
        // should implement some failsafe handling on api level for general errors
        emailSenderService.sendOtpEmail(email, plainOtp);
        log.info("OTP email sent to: {}", email);
    }

    private String generatePlainOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    @ReadWriteTransaction
    public void validateOtp(String email, String code) {
        var otpCode = otpCodeRepository.findByEmailAndIsUsedFalse(email);

        if (otpCode.isEmpty()) {
            throw new OtpVerificationException("No valid OTP found for email: " + email);
        }

        OtpCode otp = otpCode.get();
        if (OffsetDateTime.now().isAfter(otp.getExpiresAt())) {
            throw new OtpVerificationException("OTP has expired");
        }

        boolean isValid = passwordEncoder.matches(code, otp.getCodeHash());
        if (!isValid) {
            throw new OtpVerificationException("Invalid OTP code");
        }

        otp.setIsUsed(true);
        otpCodeRepository.save(otp);
        log.info("OTP validated and marked as used for email: {}", email);
    }
}
