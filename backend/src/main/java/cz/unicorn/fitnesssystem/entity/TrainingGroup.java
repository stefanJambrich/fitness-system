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
@Table(name = "training_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private String inviteCode;

    @Setter
    private Instant inviteCodeRotatedAt;

    @Setter
    private String timezone;

    @Setter
    private short defaultCancelHours;

    private Instant createdAt;

    @Setter
    private Instant deletedAt;

    public TrainingGroup(String name, String description, String inviteCode, String timezone,
                         short defaultCancelHours, Instant createdAt) {
        this.name = name;
        this.description = description;
        this.inviteCode = inviteCode;
        this.inviteCodeRotatedAt = createdAt;
        this.timezone = timezone;
        this.defaultCancelHours = defaultCancelHours;
        this.createdAt = createdAt;
    }
}