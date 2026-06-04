package net.unicorn.fitnesssystem.controller;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.api.AuthApi;
import net.unicorn.fitnesssystem.api.model.*;
import net.unicorn.fitnesssystem.helper.MessageBuilder;
import net.unicorn.fitnesssystem.service.AuthService;
import net.unicorn.fitnesssystem.service.OtpCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@CustomLog
@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final OtpCodeService otpCodeService;
    private final AuthService authService;

    @Override
    public ResponseEntity<MessageResponseDto> requestOtp(String email) {
        log.info("Received OTP request for email: {}", email);
        otpCodeService.generateAndSendOtp(email);
        log.info("OTP successfully sent to: {}", email);
        return ResponseEntity.ok(MessageBuilder.success("OTP sent to " + email));
    }

    @Override
    public ResponseEntity<OtpVerificationResponseDto> verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        OtpVerificationResponseDto result = authService.verifyOtp(otpVerificationRequestDto);
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<UserBaseDto> registerUser(RegistrationRequestDto registrationRequestDto) {
        var response = authService.registerUser(registrationRequestDto);
        return ResponseEntity.ok(response);
    }
}
