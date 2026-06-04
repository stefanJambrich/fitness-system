package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationRequestDto;
import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;
import net.unicorn.fitnesssystem.api.model.RegistrationRequestDto;

public interface AuthService {

    OtpVerificationResult verifyOtp(OtpVerificationRequestDto request);

    OtpVerificationUserResponseDto registerUser(RegistrationRequestDto request);
}
