package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationNewUserResponseDto;
import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;

public record OtpVerificationResult(
        boolean isNewUser,
        OtpVerificationNewUserResponseDto newUserResponse,
        OtpVerificationUserResponseDto existingUserResponse,
        String sessionToken
) {

    public static OtpVerificationResult forNewUser(OtpVerificationNewUserResponseDto response) {
        return new OtpVerificationResult(true, response, null, null);
    }

    public static OtpVerificationResult forExistingUser(OtpVerificationUserResponseDto response, String sessionToken) {
        return new OtpVerificationResult(false, null, response, sessionToken);
    }
}
