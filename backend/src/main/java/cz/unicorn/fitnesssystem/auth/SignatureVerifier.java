package cz.unicorn.fitnesssystem.auth;

import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.exception.ApiException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SignatureVerifier {

    private static final ECParameterSpec P256 = p256();

    /** Parses an SPKI public key
     *  throws {@link IllegalArgumentException} unless it is on curve P-256.
     */
    public ECPublicKey parsePublicKey(byte[] spki) {
        try {
            PublicKey key = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(spki));
            if (key instanceof ECPublicKey ec
                    && ec.getParams().getCurve().equals(P256.getCurve())
                    && ec.getParams().getOrder().equals(P256.getOrder())) {
                return ec;
            }
        } catch (GeneralSecurityException e) {
            // fall through
        }
        throw new IllegalArgumentException("Not an ECDSA P-256 public key");
    }

    /** Checks the request is signed by the (not yet registered) key it carries */
    public void verifyKeyPossession(byte[] spki, SignedRequest request) {
        if (!MessageDigest.isEqual(CanonicalRequest.sha256(spki), request.keyId())
                || !verify(parsePublicKey(spki), request)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH_INVALID", "Invalid request signature");
        }
    }

    public boolean verify(PublicKey key, SignedRequest request) {
        try {
            Signature verifier = Signature.getInstance("SHA256withECDSAinP1363Format");
            verifier.initVerify(key);
            verifier.update(request.canonical().getBytes(StandardCharsets.UTF_8));
            return verifier.verify(request.signature());
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    private static ECParameterSpec p256() {
        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
            params.init(new ECGenParameterSpec("secp256r1"));
            return params.getParameterSpec(ECParameterSpec.class);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}