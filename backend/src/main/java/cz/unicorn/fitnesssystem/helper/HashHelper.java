package cz.unicorn.fitnesssystem.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashHelper {

    private HashHelper() {
    }

    public static byte[] sha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}