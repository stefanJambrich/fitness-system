package cz.unicorn.fitnesssystem.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final Map<String, String> fields;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, null);
    }

    public ApiException(HttpStatus status, String code, String message, Map<String, String> fields) {
        super(message);
        this.status = status;
        this.code = code;
        this.fields = fields;
    }

    public static ApiException validation(String field, String problem) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION", "Request validation failed", Map.of(field, problem));
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}