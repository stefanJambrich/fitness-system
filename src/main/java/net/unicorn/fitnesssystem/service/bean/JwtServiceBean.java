package net.unicorn.fitnesssystem.service.bean;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.CustomLog;
import net.unicorn.fitnesssystem.annotations.ReadOnlyTransaction;
import net.unicorn.fitnesssystem.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@CustomLog
@Service
@ReadOnlyTransaction
public class JwtServiceBean implements JwtService {

    @Value("${application.security.jwt.secret}")
    private String secretKey;

    @Value("${application.security.jwt.registration-expiration}")
    private long registrationExpiration;

    @Value("${application.security.jwt.session-expiration}")
    private long sessionExpiration;

    @Override
    public String generateRegistrationToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + registrationExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .claim("type", "registration_token")
                .compact();
    }

    @Override
    public String generateSessionToken(Long userId, String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + registrationExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", "session_token")
                .claim("email", email)
                .claim("roles", roles)
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
