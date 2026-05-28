package net.unicorn.fitnesssystem.controller;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.unicorn.fitnesssystem.api.AuthApi;
import net.unicorn.fitnesssystem.api.model.MessageResponseDto;
import net.unicorn.fitnesssystem.helper.ResponseBuilder;
import net.unicorn.fitnesssystem.service.bean.OtpCodeServiceBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@CustomLog
@Controller
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {

    private final OtpCodeServiceBean otpCodeServiceBean;

    @Override
    public ResponseEntity<MessageResponseDto> requestOtp(String email) {
        log.info("Received OTP request for email: {}", email);
        otpCodeServiceBean.generateAndSendOtp(email);
        log.info("OTP successfully sent to: {}", email);
        return ResponseEntity.ok(ResponseBuilder.success("OTP sent to " + email));
    }
}
