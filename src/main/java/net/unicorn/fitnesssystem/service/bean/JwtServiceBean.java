package net.unicorn.fitnesssystem.service.bean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.CustomLog;
import net.unicorn.fitnesssystem.exceptions.RegistrationException;
import org.springframework.http.HttpStatus;
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
    public String extractEmailFromRegistrationToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!"registration_token".equals(type)) {
                throw new RegistrationException("Invalid token type", HttpStatus.UNAUTHORIZED);
            }

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RegistrationException("Registration token is invalid or expired", HttpStatus.UNAUTHORIZED);
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
