package net.unicorn.fitnesssystem.exceptions;

public class OtpVerificationException extends ApplicationException {

    public OtpVerificationException(String message) {
        super(message);
    }

    public OtpVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
