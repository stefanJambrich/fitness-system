package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentMember {

    public MemberPrincipal get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberPrincipal principal) {
            return principal;
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", "Authentication required");
    }
}