package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.annotations.ReadWriteTransaction;
import net.unicorn.fitnesssystem.api.model.*;
import net.unicorn.fitnesssystem.entity.User;
import net.unicorn.fitnesssystem.exceptions.RegistrationException;
import net.unicorn.fitnesssystem.mapper.AuthMapper;
import net.unicorn.fitnesssystem.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class AuthServiceBean implements AuthService {

    private final OtpCodeService otpCodeService;
    private final UserService userService;
    private final JwtService jwtService;
    private final DeviceService deviceService;
    private final AuthMapper authMapper;

    @Override
    @ReadWriteTransaction
    public OtpVerificationResponseDto verifyOtp(OtpVerificationRequestDto request) {
        log.info("Verifying otp code: {}", request.getCode());
        String email = request.getEmail().replaceAll("\\s+", "").toLowerCase();
        String code = request.getCode();
        OtpVerificationResponseDto response = new OtpVerificationResponseDto();

        otpCodeService.validateOtp(email, code);

        if (!userService.userExists(email)) {
            log.info("New user detected for email: {}", email);
            String registerToken = jwtService.generateRegistrationToken(email);

            response.setStatus("REGISTRATION_REQUIRED");
            response.setRegistrationToken(registerToken);

            return response;
        }

        log.info("Existing user found for email: {}", email);
        response.setUser(userService.getExistingUserByEmail(email));
        UserBaseDto user = response.getUser();

        deviceService.updateUserDevice(user.getId().longValue(), request.getPublicHashKey());
        log.info("Device registered/updated for member: {}", email);

        return response;
    }

    @Override
    @ReadWriteTransaction
    public UserBaseDto registerUser(RegistrationRequestDto request) {
        String email = jwtService.extractEmailFromRegistrationToken(request.getRegistrationToken());

        if (userService.userExists(email)) {
            throw new RegistrationException("User with this email already exists", HttpStatus.CONFLICT);
        }

        if (deviceService.deviceKeyExists(request.getPublicKeyHash())) {
            throw new RegistrationException("Device key already registered", HttpStatus.CONFLICT);
        }

        User newUser = userService.createUser(email, request.getName(), request.getRegisterAsTrainer());

        deviceService.updateUserDevice(newUser.getId(), request.getPublicKeyHash());
        log.info("Registered new user and device for email: {}", email);

        return authMapper.toUserBaseDto(newUser);
    }
}
