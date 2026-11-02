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
@Table(name = "member_device")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    private byte[] publicKey;

    private byte[] publicKeyHash;

    private String algorithm;

    @Setter
    private String label;

    private Instant createdAt;

    @Setter
    private Instant lastSeenAt;

    @Setter
    private Instant revokedAt;

    public MemberDevice(Member member, byte[] publicKey, byte[] publicKeyHash, String algorithm, String label,
                        Instant createdAt) {
        this.member = member;
        this.publicKey = publicKey;
        this.publicKeyHash = publicKeyHash;
        this.algorithm = algorithm;
        this.label = label;
        this.createdAt = createdAt;
        this.lastSeenAt = createdAt;
    }
}