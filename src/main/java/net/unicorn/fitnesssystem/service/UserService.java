package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;
import net.unicorn.fitnesssystem.entity.User;

public interface UserService {

    boolean userExists(String email);

    OtpVerificationUserResponseDto getExistingUserByEmail(String email);

    User createUser(String email, String fullname, boolean registerAsTrainer);
}
