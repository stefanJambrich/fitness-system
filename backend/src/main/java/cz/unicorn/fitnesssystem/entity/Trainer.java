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
@Table(name = "trainer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    @Setter
    private String displayName;

    @Setter
    private Instant emailVerifiedAt;

    private Instant createdAt;

    @Setter
    private Instant deactivatedAt;

    public Trainer(String email, String displayName, Instant createdAt) {
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }
}