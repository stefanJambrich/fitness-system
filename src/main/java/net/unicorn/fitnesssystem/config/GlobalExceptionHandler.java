package net.unicorn.fitnesssystem.config;

import lombok.CustomLog;
import net.unicorn.fitnesssystem.api.model.MessageResponseDto;
import net.unicorn.fitnesssystem.exceptions.ApplicationException;
import net.unicorn.fitnesssystem.exceptions.OtpVerificationException;
import net.unicorn.fitnesssystem.helper.MessageBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@CustomLog
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OtpVerificationException.class)
    public ResponseEntity<MessageResponseDto> handleOtpVerificationException(OtpVerificationException ex) {
        log.error("OTP verification failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON)
                .body(MessageBuilder.error(ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<MessageResponseDto> handleApplicationException(ApplicationException ex) {
        log.error("Application exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                .body(MessageBuilder.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDto> handleGeneralException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                .body(MessageBuilder.error(ex.getMessage()));
    }
}
