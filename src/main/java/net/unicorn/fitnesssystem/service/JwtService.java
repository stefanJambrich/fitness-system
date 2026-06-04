package net.unicorn.fitnesssystem.service;

public interface JwtService {

    String generateRegistrationToken(String email);

    String extractEmailFromRegistrationToken(String token);
}
