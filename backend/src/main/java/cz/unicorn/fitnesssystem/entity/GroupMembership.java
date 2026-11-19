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
@Table(name = "group_membership")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    private TrainingGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    private Instant joinedAt;

    @Setter
    private Instant rejoinedAt;

    @Setter
    private Instant leftAt;

    @Setter
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private MembershipEnd endReason;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "removed_by_trainer_id")
    private Trainer removedByTrainer;

    public void rejoin(Instant now) {
        this.rejoinedAt = now;
        this.leftAt = null;
        this.endReason = null;
    }
}