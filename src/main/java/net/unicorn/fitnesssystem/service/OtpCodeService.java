package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationRequestDto;

public interface OtpCodeService {

    void generateAndSendOtp(String email);

    void validateOtp(OtpVerificationRequestDto otpVerificationRequestDto);
}
