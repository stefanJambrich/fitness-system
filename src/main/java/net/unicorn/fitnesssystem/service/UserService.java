package net.unicorn.fitnesssystem.service;

import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.entity.User;

public interface UserService {

    boolean userExists(String email);

    UserBaseDto getExistingUserByEmail(String email);

    User createUser(String email, String fullname, boolean registerAsTrainer);
}
