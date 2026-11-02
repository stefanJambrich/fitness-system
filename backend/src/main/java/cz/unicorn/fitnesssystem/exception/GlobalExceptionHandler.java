package cz.unicorn.fitnesssystem.exception;

import cz.unicorn.fitnesssystem.api.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException e) {
        ErrorResponse body = new ErrorResponse(e.getCode(), e.getMessage()).fields(e.getFields());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBody(MethodArgumentNotValidException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return validation(fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleParams(ConstraintViolationException e) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : e.getConstraintViolations()) {
            String path = v.getPropertyPath().toString();
            fields.putIfAbsent(path.substring(path.lastIndexOf('.') + 1), v.getMessage());
        }
        return validation(fields);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        return validation(Map.of(e.getParameterName(), "is required"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return validation(Map.of(e.getName(), "has an invalid format"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION", "Malformed request body");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException e) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION", "Unsupported content type");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException e) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Resource not found");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException e) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Method " + e.getMethod() + " is not allowed");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException e) {
        return error(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", "Authentication required");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL", "Internal server error");
    }

    private ResponseEntity<ErrorResponse> validation(Map<String, String> fields) {
        ErrorResponse body = new ErrorResponse("VALIDATION", "Request validation failed").fields(fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }
}