package cz.unicorn.fitnesssystem.helper;

import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.dto.TrainerPrincipal;
import cz.unicorn.fitnesssystem.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Gives controllers the authenticated caller of the current request. Needed because controllers implement the
 * generated API interfaces and can't take {@code @AuthenticationPrincipal} parameters.
 */
@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final HttpServletRequest request;

    public MemberPrincipal currentMember() {
        return principal(MemberPrincipal.class);
    }

    public TrainerPrincipal currentTrainer() {
        return principal(TrainerPrincipal.class);
    }

    /** The signature of a registration request, checked by the filter before the key is known to the server. */
    public SignedRequest registrationProof() {
        if (request.getAttribute(SignedRequest.ATTRIBUTE) instanceof SignedRequest proof) {
            return proof;
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", "Authentication required");
    }

    private static <T> T principal(Class<T> type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && type.isInstance(authentication.getPrincipal())) {
            return type.cast(authentication.getPrincipal());
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", "Authentication required");
    }
}