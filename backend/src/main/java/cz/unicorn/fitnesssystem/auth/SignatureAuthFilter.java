package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Parses the BR-01 headers, checks clock skew and exposes a {@link SignedRequest} attribute.
 */
@RequiredArgsConstructor
public class SignatureAuthFilter extends OncePerRequestFilter {

    public static final String KEY_ID = "X-Key-Id";
    public static final String TIMESTAMP = "X-Timestamp";
    public static final String NONCE = "X-Nonce";
    public static final String SIGNATURE = "X-Signature";

    private static final int KEY_ID_BYTES = 32;
    private static final int NONCE_BYTES = 16;
    private static final int SIGNATURE_BYTES = 64;

    private final Clock clock;
    private final AppProperties.Signature properties;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(HttpMethod.POST.matches(request.getMethod()) && "/api/members".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        CachedBodyRequest cached = new CachedBodyRequest(request);
        try {
            cached.setAttribute(SignedRequest.ATTRIBUTE, parse(cached));
        } catch (ApiException e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }
        chain.doFilter(cached, response);
    }

    private SignedRequest parse(CachedBodyRequest request) {
        String keyId = request.getHeader(KEY_ID);
        String timestamp = request.getHeader(TIMESTAMP);
        String nonce = request.getHeader(NONCE);
        String signature = request.getHeader(SIGNATURE);
        if (keyId == null || timestamp == null || nonce == null || signature == null) {
            throw invalid("Missing signature headers");
        }

        Instant signedAt;
        try {
            signedAt = Instant.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw invalid("Malformed " + TIMESTAMP);
        }
        if (Duration.between(signedAt, clock.instant()).abs().compareTo(properties.maxClockSkew()) > 0) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "CLOCK_SKEW", "Request timestamp is too far from server time");
        }

        String pathAndQuery = request.getQueryString() == null
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + request.getQueryString();
        return new SignedRequest(
                decode(keyId, KEY_ID, KEY_ID_BYTES),
                signedAt,
                decode(nonce, NONCE, NONCE_BYTES),
                decode(signature, SIGNATURE, SIGNATURE_BYTES),
                CanonicalRequest.of(request.getMethod(), pathAndQuery, timestamp, nonce, request.getBody()));
    }

    private static byte[] decode(String value, String header, int expectedLength) {
        try {
            byte[] bytes = Base64Url.decode(value);
            if (bytes.length == expectedLength) {
                return bytes;
            }
        } catch (IllegalArgumentException e) {
            // fall through
        }
        throw invalid("Malformed " + header);
    }

    private static ApiException invalid(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", message);
    }
}