package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestNonceId implements Serializable {

    private UUID deviceId;

    private byte[] nonce;

    @Override
    public boolean equals(Object o) {
        return o instanceof RequestNonceId other
                && Objects.equals(deviceId, other.deviceId)
                && Arrays.equals(nonce, other.nonce);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(deviceId) + Arrays.hashCode(nonce);
    }
}