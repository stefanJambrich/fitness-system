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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "training_series")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private TrainingGroup group;

    private String title;

    private String location;

    private short weekday;

    private LocalTime startTime;

    private short durationMinutes;

    private Short capacity;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private LocalDate generatedUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_trainer_id")
    private Trainer createdByTrainer;

    private Instant createdAt;

    @Setter
    private Instant endedAt;
}