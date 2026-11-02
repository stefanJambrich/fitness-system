package cz.unicorn.fitnesssystem.dto;

import java.time.Instant;

public record SignedRequest(byte[] keyId, Instant timestamp, byte[] nonce, byte[] signature, String canonical) {

    public static final String ATTRIBUTE = SignedRequest.class.getName();
}