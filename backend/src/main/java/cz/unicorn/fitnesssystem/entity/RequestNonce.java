package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "request_nonce")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestNonce implements Persistable<RequestNonceId> {

    @EmbeddedId
    private RequestNonceId id;

    private Instant expiresAt;

    // Always insert, never merge: a duplicate (device_id, nonce) must fail on the primary key as a replay.
    @Override
    public boolean isNew() {
        return true;
    }
}