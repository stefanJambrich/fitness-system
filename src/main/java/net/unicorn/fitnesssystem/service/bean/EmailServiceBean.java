package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.service.EmailService;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@ReadOnlyTransaction
public class EmailServiceBean implements EmailService {

    @Override
    public void sendOtpEmail(String email, String otp) {
        // TODO: implement email sending
    }
}

