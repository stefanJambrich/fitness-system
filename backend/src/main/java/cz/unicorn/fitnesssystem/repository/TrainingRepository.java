package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.Training;
import cz.unicorn.fitnesssystem.entity.TrainingStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TrainingRepository extends JpaRepository<Training, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Training> findAllWithLockByGroupIdAndStatusAndStartsAtAfterOrderById(
            UUID groupId, TrainingStatus status, Instant after);

    Optional<Training> findFirstByGroupIdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
            UUID groupId, TrainingStatus status, Instant after);
}