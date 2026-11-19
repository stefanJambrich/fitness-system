package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.constant.ApiPaths;
import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.helper.HashHelper;
import cz.unicorn.fitnesssystem.service.MemberAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Authenticates every {@code /api/me} and {@code /api/invites} request. Registration has no device to look up yet,
 * so it only gets a clock-skew check and the parsed {@link SignedRequest} as a request attribute.
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

    private static final List<GrantedAuthority> MEMBER_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_MEMBER");

    private final MemberAuthService memberAuthService;
    private final SignatureVerifier signatureVerifier;
    private final HandlerExceptionResolver exceptionResolver;
    private final AppProperties properties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isRegistration(request) && !isMemberApi(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        CachedBodyRequest cached;
        try {
            cached = new CachedBodyRequest(request, properties.signature().maxBodySize());
            SignedRequest signed = parse(cached);
            if (isRegistration(cached)) {
                signatureVerifier.checkClockSkew(signed);
                cached.setAttribute(SignedRequest.ATTRIBUTE, signed);
            } else {
                signIn(memberAuthService.authenticate(signed));
            }
        } catch (RuntimeException e) {
            exceptionResolver.resolveException(request, response, null, e);
            return;
        }
        chain.doFilter(cached, response);
    }

    private static boolean isRegistration(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod()) && ApiPaths.MEMBERS.equals(request.getRequestURI());
    }

    private static boolean isMemberApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals(ApiPaths.MEMBER_API) || uri.startsWith(ApiPaths.MEMBER_API + "/")
                || uri.startsWith(ApiPaths.INVITES + "/");
    }

    private static void signIn(MemberPrincipal principal) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal, null, MEMBER_AUTHORITIES));
        SecurityContextHolder.setContext(context);
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

        return new SignedRequest(
                decode(keyId, KEY_ID, KEY_ID_BYTES),
                signedAt,
                decode(nonce, NONCE, NONCE_BYTES),
                decode(signature, SIGNATURE, SIGNATURE_BYTES),
                canonicalString(request, timestamp, nonce));
    }

    //canonical string, must match frontend/src/crypto/signer.ts byte for byte.
    private static String canonicalString(CachedBodyRequest request, String timestamp, String nonce) {
        String pathAndQuery = request.getQueryString() == null
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + request.getQueryString();
        return String.join("\n", request.getMethod(), pathAndQuery, timestamp, nonce,
                Base64Url.encode(HashHelper.sha256(request.getBody())));
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