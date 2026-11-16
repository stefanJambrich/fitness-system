package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.auth.SignatureVerifier;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.MemberPrincipal;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.MemberDevice;
import cz.unicorn.fitnesssystem.entity.RequestNonce;
import cz.unicorn.fitnesssystem.entity.RequestNonceId;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.MemberDeviceRepository;
import cz.unicorn.fitnesssystem.repository.RequestNonceRepository;
import cz.unicorn.fitnesssystem.service.MemberAuthService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberDeviceRepository deviceRepository;
    private final RequestNonceRepository nonceRepository;
    private final SignatureVerifier signatureVerifier;
    private final AppProperties properties;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public MemberPrincipal authenticate(SignedRequest request) {
        MemberDevice device = deviceRepository.findWithMemberByPublicKeyHash(request.keyId())
                .orElseThrow(() -> unauthorized("DEVICE_UNKNOWN", "This device is not registered"));
        if (device.getRevokedAt() != null) {
            throw unauthorized("DEVICE_REVOKED", "This device has been revoked");
        }
        if (device.getMember().getDeletedAt() != null) {
            throw unauthorized("MEMBER_DELETED", "This identity has been deleted");
        }
        signatureVerifier.checkClockSkew(request);
        if (!signatureVerifier.verify(signatureVerifier.parsePublicKey(device.getPublicKey()), request)) {
            throw unauthorized("AUTH_INVALID", "Invalid request signature");
        }
        consumeNonce(device, request);
        touchLastSeen(device);
        return new MemberPrincipal(device.getMember().getId(), device.getId());
    }

    private void consumeNonce(MemberDevice device, SignedRequest request) {
        try {
            nonceRepository.saveAndFlush(new RequestNonce(
                    new RequestNonceId(device.getId(), request.nonce()),
                    request.timestamp().plus(properties.signature().nonceTtl())));
        } catch (DataIntegrityViolationException e) {
            throw unauthorized("REPLAY", "This request has already been processed");
        }
    }

    private void touchLastSeen(MemberDevice device) {
        Instant now = clock.instant();
        Instant lastSeen = device.getLastSeenAt();
        if (lastSeen == null || Duration.between(lastSeen, now).compareTo(properties.signature().lastSeenInterval()) >= 0) {
            device.setLastSeenAt(now);
        }
    }

    private static ApiException unauthorized(String code, String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, code, message);
    }
}