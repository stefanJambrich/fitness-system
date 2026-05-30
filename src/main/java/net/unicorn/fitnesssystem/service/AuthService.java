package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationRequestDto;

public interface AuthService {

    OtpVerificationResult verifyOtp(OtpVerificationRequestDto request);
}
