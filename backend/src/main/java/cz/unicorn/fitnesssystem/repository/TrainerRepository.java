package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.Trainer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainerRepository extends JpaRepository<Trainer, UUID> {

    Optional<Trainer> findByEmail(String email);

    // Native: advisory lock until commit, so concurrent requests for one e-mail can't create the trainer twice
    // or leave two live codes.
    @Query(value = "SELECT 1 FROM pg_advisory_xact_lock(hashtext('trainer:' || :email))", nativeQuery = true)
    int lockEmail(String email);
}