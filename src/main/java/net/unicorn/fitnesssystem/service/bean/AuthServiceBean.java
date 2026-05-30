package net.unicorn.fitnesssystem.service.bean;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.api.model.OtpVerificationNewUserResponseDto;
import net.unicorn.fitnesssystem.api.model.OtpVerificationRequestDto;
import net.unicorn.fitnesssystem.api.model.OtpVerificationUserResponseDto;
import net.unicorn.fitnesssystem.api.model.UserBaseDto;
import net.unicorn.fitnesssystem.service.*;
import org.springframework.stereotype.Service;

import java.util.List;

@CustomLog
@Service
@RequiredArgsConstructor
@ReadOnlyTransaction
public class AuthServiceBean implements AuthService {

    private final OtpCodeService otpCodeService;
    private final UserService userService;
    private final JwtService jwtService;
    private final DeviceService deviceService;

    @Override
    public OtpVerificationResult verifyOtp(OtpVerificationRequestDto request) {
        String email = request.getEmail().replaceAll("\\s+", "").toLowerCase();
        String code = request.getCode();

        otpCodeService.validateOtp(email, code);

        if (!userService.userExists(email)) {
            log.info("New user detected for email: {}", email);
            String registerToken = jwtService.generateRegistrationToken(email);

            OtpVerificationNewUserResponseDto newUserResponse = new OtpVerificationNewUserResponseDto();
            newUserResponse.setStatus("REGISTRATION_REQUIRED");
            newUserResponse.setRegisterToken(registerToken);

            return OtpVerificationResult.forNewUser(newUserResponse);
        }

        log.info("Existing user found for email: {}", email);
        OtpVerificationUserResponseDto existingUserResponse = userService.getExistingUserByEmail(email);
        UserBaseDto user = existingUserResponse.getUser();

        String sessionToken = null;
        if (UserBaseDto.RoleEnum.TRAINER.equals(user.getRole())) {
            sessionToken = jwtService.generateSessionToken(
                    user.getId().longValue(),
                    user.getEmail(),
                    List.of(user.getRole().getValue())
            );
            log.info("Generated session token for trainer: {}", email);
        } else {
            deviceService.updateUserDevice(user.getId().longValue(), request.getPublicHashKey());
            log.info("Device registered/updated for member: {}", email);
        }

        return OtpVerificationResult.forExistingUser(existingUserResponse, sessionToken);
    }
}
