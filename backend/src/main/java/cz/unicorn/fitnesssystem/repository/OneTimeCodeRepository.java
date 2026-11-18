package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.OneTimeCode;
import cz.unicorn.fitnesssystem.entity.OtpPurpose;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface OneTimeCodeRepository extends JpaRepository<OneTimeCode, UUID> {

    List<OneTimeCode> findAllByEmailAndPurposeAndConsumedAtIsNull(String email, OtpPurpose purpose);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OneTimeCode> findFirstWithLockByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(
            String email, OtpPurpose purpose);
}