package net.unicorn.fitnesssystem.controller;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.api.AuthApi;
import net.unicorn.fitnesssystem.api.model.*;
import net.unicorn.fitnesssystem.helper.MessageBuilder;
import net.unicorn.fitnesssystem.service.AuthService;
import net.unicorn.fitnesssystem.service.OtpCodeService;
import net.unicorn.fitnesssystem.service.OtpVerificationResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<OtpVerifyResponseDto> verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
        OtpVerificationResult result = authService.verifyOtp(otpVerificationRequestDto);

        if (result.isNewUser()) {
            return ResponseEntity.accepted().body(result.newUserResponse());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
        if (result.sessionToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("session_token", result.sessionToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(14400)
                    .build();
            responseBuilder.header(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return responseBuilder.body(result.existingUserResponse());
    }
}
