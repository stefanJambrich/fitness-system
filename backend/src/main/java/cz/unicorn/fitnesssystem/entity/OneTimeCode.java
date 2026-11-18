package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "one_time_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OneTimeCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private OtpPurpose purpose;

    private String email;

    private byte[] codeHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    private short attempts;

    private Instant createdAt;

    private Instant expiresAt;

    @Setter
    private Instant consumedAt;

    public OneTimeCode(OtpPurpose purpose, String email, byte[] codeHash, Trainer trainer, Member member,
                       Instant createdAt, Instant expiresAt) {
        this.purpose = purpose;
        this.email = email;
        this.codeHash = codeHash;
        this.trainer = trainer;
        this.member = member;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}