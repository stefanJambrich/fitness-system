package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.TrainerSession;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerSessionRepository extends JpaRepository<TrainerSession, UUID> {

    @EntityGraph(attributePaths = "trainer")
    Optional<TrainerSession> findWithTrainerByTokenHash(byte[] tokenHash);

    List<TrainerSession> findAllByTrainerIdAndRevokedAtIsNullAndExpiresAtAfterOrderByLastUsedAtDesc(
            UUID trainerId, Instant now);

    Optional<TrainerSession> findByIdAndTrainerIdAndRevokedAtIsNullAndExpiresAtAfter(
            UUID id, UUID trainerId, Instant now);
}