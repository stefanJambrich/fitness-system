package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.exceptions.ApplicationException;
import net.unicorn.fitnesssystem.mapper.AuthMapper;
import net.unicorn.fitnesssystem.repository.UserRepository;
import net.unicorn.fitnesssystem.service.UserService;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class UserServiceBean implements UserService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public OtpVerificationUserResponseDto getExistingUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("User not found for email: " + email));

        return authMapper.toExistingUserResponse(user);
    }
}
