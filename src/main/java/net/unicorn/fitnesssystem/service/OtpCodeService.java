package net.unicorn.fitnesssystem.service;

public interface OtpCodeService {

    void generateAndSendOtp(String email);

    void validateOtp(String email, String code);
}
