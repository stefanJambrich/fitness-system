package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.constant.ApiPaths;
import cz.unicorn.fitnesssystem.dto.TrainerPrincipal;
import cz.unicorn.fitnesssystem.service.TrainerSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Authenticates {@code /api/trainer} requests that carry a session cookie. Requests without one stay anonymous,
 * so the login endpoints work and protected ones get {@code 401} from the security chain.
 */
@RequiredArgsConstructor
public class TrainerSessionFilter extends OncePerRequestFilter {

    private static final List<GrantedAuthority> TRAINER_AUTHORITIES = AuthorityUtils.createAuthorityList("ROLE_TRAINER");

    private final TrainerSessionService sessionService;
    private final TrainerSessionCookie sessionCookie;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith(ApiPaths.TRAINER_API + "/") || ApiPaths.TRAINER_PUBLIC.contains(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Optional<String> token = sessionCookie.read(request);
        if (token.isPresent()) {
            try {
                TrainerPrincipal principal = sessionService.authenticate(token.get());
                signIn(principal);
                if (principal.remembered()) {
                    response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.issue(token.get(), true).toString());
                }
            } catch (RuntimeException e) {
                response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.clear().toString());
                exceptionResolver.resolveException(request, response, null, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static void signIn(TrainerPrincipal principal) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal, null, TRAINER_AUTHORITIES));
        SecurityContextHolder.setContext(context);
    }
}