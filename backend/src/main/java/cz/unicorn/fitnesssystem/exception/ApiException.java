package cz.unicorn.fitnesssystem.exception;

import org.springframework.http.HttpStatus;

/** Business error rendered as {@code { "error": code, "message": message }} (section 1.4). */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}