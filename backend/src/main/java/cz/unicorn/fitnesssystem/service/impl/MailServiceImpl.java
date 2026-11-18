package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.OtpIssued;
import cz.unicorn.fitnesssystem.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final AppProperties properties;

    /** {@inheritDoc} */
    @Override
    @Async
    @TransactionalEventListener
    public void sendOtp(OtpIssued issued) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.mail().from());
        message.setTo(issued.email());
        message.setSubject("Your login code: " + issued.code());
        message.setText("""
                Your login code is %s

                It is valid for %d minutes. If you did not ask for it, you can ignore this e-mail.
                """.formatted(issued.code(), properties.otp().ttl().toMinutes()));
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.warn("Sending a one-time code failed: {}", e.getClass().getSimpleName());
        }
    }
}