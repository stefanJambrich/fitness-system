package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class EmailSenderServiceBean implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${application.mail.from-address}")
    private String fromAddress;

    @Override
    public void sendOtpEmail(String email, String otp) {
        log.info("Preparing to send OTP email to: {}", email);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject("Your OTP Verification Code");
            message.setText("Hello,\n\nYour OTP verification code is: " + otp + "\n\nThis code will expire in 10 minutes.\n\nBest regards,\nFitness System Team");

            mailSender.send(message);
            log.info("OTP email successfully sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}

