package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainer_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private byte[] tokenHash;

    private boolean remembered;

    private String deviceLabel;

    private Instant createdAt;

    @Setter
    private Instant lastUsedAt;

    @Setter
    private Instant expiresAt;

    @Setter
    private Instant revokedAt;

    public TrainerSession(Trainer trainer, byte[] tokenHash, boolean remembered, String deviceLabel,
                          Instant createdAt, Instant expiresAt) {
        this.trainer = trainer;
        this.tokenHash = tokenHash;
        this.remembered = remembered;
        this.deviceLabel = deviceLabel;
        this.createdAt = createdAt;
        this.lastUsedAt = createdAt;
        this.expiresAt = expiresAt;
    }
}