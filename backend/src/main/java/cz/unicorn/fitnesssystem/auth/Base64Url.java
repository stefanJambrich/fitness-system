package cz.unicorn.fitnesssystem.auth;

import java.util.Base64;

public final class Base64Url {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private Base64Url() {
    }

    public static String encode(byte[] bytes) {
        return ENCODER.encodeToString(bytes);
    }

    public static byte[] decode(String value) {
        return DECODER.decode(value);
    }
}