package cz.unicorn.fitnesssystem.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CanonicalRequest {

    private CanonicalRequest() {
    }

    public static String of(String method, String pathAndQuery, String timestamp, String nonce, byte[] body) {
        return String.join("\n", method, pathAndQuery, timestamp, nonce, Base64Url.encode(sha256(body)));
    }

    public static byte[] sha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}