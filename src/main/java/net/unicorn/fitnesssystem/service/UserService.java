package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;

public interface UserService {

    boolean userExists(String email);

    OtpVerificationUserResponseDto getExistingUserByEmail(String email);
}
