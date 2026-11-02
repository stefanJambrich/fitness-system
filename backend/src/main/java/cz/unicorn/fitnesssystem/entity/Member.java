package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private String nickname;

    @Setter
    private String code;

    @Setter
    private String recoveryEmail;

    @Setter
    private Instant recoveryEmailVerifiedAt;

    private Instant createdAt;

    @Setter
    private Instant deletedAt;

    public Member(String nickname, String code, Instant createdAt) {
        this.nickname = nickname;
        this.code = code;
        this.createdAt = createdAt;
    }
}