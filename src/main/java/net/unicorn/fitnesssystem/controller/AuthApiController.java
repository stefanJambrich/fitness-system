package net.unicorn.fitnesssystem.controller;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.api.AuthApi;
import net.unicorn.fitnesssystem.api.model.*;
import net.unicorn.fitnesssystem.enums.UserRoleEnum;
import net.unicorn.fitnesssystem.helper.MessageBuilder;
import net.unicorn.fitnesssystem.service.DeviceService;
import net.unicorn.fitnesssystem.service.JwtService;
import net.unicorn.fitnesssystem.service.OtpCodeService;
import net.unicorn.fitnesssystem.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CustomLog
@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final OtpCodeService otpCodeService;
    private final UserService userService;
    private final JwtService jwtService;
    private final DeviceService deviceService;

    @Override
    public ResponseEntity<MessageResponseDto> requestOtp(String email) {
        log.info("Received OTP request for email: {}", email);
        otpCodeService.generateAndSendOtp(email);
        log.info("OTP successfully sent to: {}", email);
        return ResponseEntity.ok(MessageBuilder.success("OTP sent to " + email));
    }

    @Override
    public ResponseEntity<OtpVerifyResponseDto> verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        otpCodeService.validateOtp(otpVerificationRequestDto);
        if (userService.userExists(otpVerificationRequestDto.getEmail())) {
            log.info("User with email {} exists", otpVerificationRequestDto.getEmail());
            OtpVerificationNewUserResponseDto responseDto = new OtpVerificationNewUserResponseDto();
            responseDto.setStatus("REGISTRATION_REQUIRED");
            responseDto.setRegisterToken(jwtService.generateRegistrationToken(otpVerificationRequestDto.getEmail()));

            return ResponseEntity.accepted().body(responseDto);
        }
        var responseExistingUserDto = userService.getExistingUserByEmail(otpVerificationRequestDto.getEmail());
        if (responseExistingUserDto.getAuthMethod().equals(OtpVerificationUserResponseDto.AuthMethodEnum.SESSION_COOKIE)) {
            String sessionToken = jwtService.generateSessionToken(
                    responseExistingUserDto.getUser().getId().longValue(),
                    responseExistingUserDto.getUser().getEmail(),
                    List.of(UserRoleEnum.TRAINER.toString())
            );

            ResponseCookie cookie = ResponseCookie.from("session_token", sessionToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(14400)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(responseExistingUserDto);
        }

        deviceService.updateUserDevice(responseExistingUserDto.getUser().getId().longValue(), otpVerificationRequestDto.getPublicHashKey());
        return ResponseEntity.ok(responseExistingUserDto);
    }


}
