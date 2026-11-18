package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.constant.ApiPaths;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@RequiredArgsConstructor
public class TrainerSessionCookie {

    private final AppProperties properties;

    /** A remembered session survives browser restarts; any other one ends with the browser session. */
    public ResponseCookie issue(String token, boolean remembered) {
        ResponseCookie.ResponseCookieBuilder cookie = base(token);
        if (remembered) {
            cookie.maxAge(properties.trainerSession().rememberedLifetime());
        }
        return cookie.build();
    }

    public ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    public Optional<String> read(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, properties.trainerSession().cookieName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isEmpty());
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(properties.trainerSession().cookieName(), value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(ApiPaths.TRAINER_API);
    }
}