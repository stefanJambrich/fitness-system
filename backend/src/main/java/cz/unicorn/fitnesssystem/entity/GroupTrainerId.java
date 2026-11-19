package cz.unicorn.fitnesssystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GroupTrainerId implements Serializable {

    private UUID groupId;

    @Column(name = "trainer_id")
    private UUID trainerId;

    @Override
    public boolean equals(Object o) {
        return o instanceof GroupTrainerId other
                && Objects.equals(groupId, other.groupId)
                && Objects.equals(trainerId, other.trainerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, trainerId);
    }
}