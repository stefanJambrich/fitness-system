package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "group_trainer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupTrainer {

    @EmbeddedId
    private GroupTrainerId id;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private TrainingGroup group;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private GroupTrainerRole role;

    private Instant addedAt;

    public GroupTrainer(TrainingGroup group, UUID trainerId, GroupTrainerRole role, Instant addedAt) {
        this.id = new GroupTrainerId(group.getId(), trainerId);
        this.group = group;
        this.role = role;
        this.addedAt = addedAt;
    }
}