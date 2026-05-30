package net.unicorn.fitnesssystem.service;

public interface EmailSenderService {
    void sendOtpEmail(String email, String otp);
}

