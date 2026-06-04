package net.unicorn.fitnesssystem.service;

import java.util.List;

public interface JwtService {

    String generateRegistrationToken(String email);

    String generateSessionToken(Long userId, String email, List<String> roles);

    String extractEmailFromRegistrationToken(String token);
}
