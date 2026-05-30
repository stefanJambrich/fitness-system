package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.service.EmailSenderService;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@ReadOnlyTransaction
public class EmailSenderServiceBean implements EmailSenderService {

    @Override
    public void sendOtpEmail(String email, String otp) {
        // TODO: implement email sending
    }
}

