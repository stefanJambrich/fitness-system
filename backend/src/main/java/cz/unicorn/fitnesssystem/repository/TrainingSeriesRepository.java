package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.TrainingSeries;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSeriesRepository extends JpaRepository<TrainingSeries, UUID> {

    List<TrainingSeries> findAllByGroupIdAndEndedAtIsNull(UUID groupId);
}