package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.auth.Base64Url;
import cz.unicorn.fitnesssystem.auth.SignatureVerifier;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.Member;
import cz.unicorn.fitnesssystem.entity.MemberDevice;
import cz.unicorn.fitnesssystem.entity.RequestNonce;
import cz.unicorn.fitnesssystem.entity.RequestNonceId;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.MemberDeviceRepository;
import cz.unicorn.fitnesssystem.repository.MemberRepository;
import cz.unicorn.fitnesssystem.repository.RequestNonceRepository;
import cz.unicorn.fitnesssystem.service.MemberService;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 4;
    private static final int CODE_ATTEMPTS = 5;

    private final SecureRandom random = new SecureRandom();

    private final MemberRepository memberRepository;
    private final MemberDeviceRepository deviceRepository;
    private final RequestNonceRepository nonceRepository;
    private final SignatureVerifier signatureVerifier;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public Member createMember(NewMember newMember, SignedRequest proof) {
        String nickname = newMember.nickname().strip();
        if (nickname.isEmpty()) {
            throw ApiException.validation("nickname", "must not be blank");
        }
        byte[] publicKey = decodePublicKey(newMember.publicKey());
        signatureVerifier.verifyKeyPossession(publicKey, proof);
        if (deviceRepository.existsByPublicKeyHash(proof.keyId())) {
            throw new ApiException(HttpStatus.CONFLICT, "KEY_ALREADY_REGISTERED", "This key is already registered");
        }

        Instant now = clock.instant();
        Member member = memberRepository.save(new Member(nickname, uniqueCode(nickname), now));
        MemberDevice device = deviceRepository.save(new MemberDevice(
                member, publicKey, proof.keyId(), newMember.algorithm(), newMember.deviceLabel(), now));
        nonceRepository.save(new RequestNonce(
                new RequestNonceId(device.getId(), proof.nonce()),
                proof.timestamp().plus(properties.signature().nonceTtl())));
        return member;
    }

    private byte[] decodePublicKey(String encoded) {
        try {
            byte[] spki = Base64Url.decode(encoded);
            signatureVerifier.parsePublicKey(spki);
            return spki;
        } catch (IllegalArgumentException e) {
            throw ApiException.validation("publicKey", "must be a base64url SPKI ECDSA P-256 public key");
        }
    }

    private String uniqueCode(String nickname) {
        for (int attempt = 0; attempt < CODE_ATTEMPTS; attempt++) {
            String code = randomCode();
            if (!memberRepository.existsByNicknameAndCode(nickname, code)) {
                return code;
            }
        }
        throw new IllegalStateException("No free code for nickname after " + CODE_ATTEMPTS + " attempts");
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_ALPHABET.charAt(random.nextInt(CODE_ALPHABET.length())));
        }
        return code.toString();
    }
}