package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import cz.unicorn.fitnesssystem.entity.GroupTrainerId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupTrainerRepository extends JpaRepository<GroupTrainer, GroupTrainerId> {

    @EntityGraph(attributePaths = "group")
    List<GroupTrainer> findAllByIdTrainerIdAndGroupDeletedAtIsNullOrderByGroupNameAsc(UUID trainerId);

    @EntityGraph(attributePaths = "group")
    Optional<GroupTrainer> findWithGroupById(GroupTrainerId id);
}