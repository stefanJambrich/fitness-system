package net.unicorn.fitnesssystem.service;

public interface OtpCodeService {

    void generateAndSendOtp(String email);

    boolean validateOtp(String email, String plainOtp);
}
