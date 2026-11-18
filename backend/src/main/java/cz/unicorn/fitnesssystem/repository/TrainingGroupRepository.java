package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.TrainingGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingGroupRepository extends JpaRepository<TrainingGroup, UUID> {

    boolean existsByInviteCode(String inviteCode);

    boolean existsByIdAndDeletedAtIsNull(UUID id);
}