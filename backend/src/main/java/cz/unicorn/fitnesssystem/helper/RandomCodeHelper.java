package cz.unicorn.fitnesssystem.helper;

import java.security.SecureRandom;

public final class RandomCodeHelper {

    // No 0/O or 1/I, so codes can be read aloud and retyped without mistakes.
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String DIGITS = "0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomCodeHelper() {
    }

    public static String code(int length) {
        return draw(ALPHABET, length);
    }

    public static String digits(int length) {
        return draw(DIGITS, length);
    }

    public static byte[] token(int bytes) {
        byte[] token = new byte[bytes];
        RANDOM.nextBytes(token);
        return token;
    }

    private static String draw(String alphabet, int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return code.toString();
    }
}