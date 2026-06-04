package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationRequestDto;
import net.unicorn.fitnesssystem.api.model.OtpVerificationResponseDto;
import net.unicorn.fitnesssystem.api.model.RegistrationRequestDto;
import net.unicorn.fitnesssystem.api.model.UserBaseDto;

public interface AuthService {

    OtpVerificationResponseDto verifyOtp(OtpVerificationRequestDto request);

    UserBaseDto registerUser(RegistrationRequestDto request);
}
