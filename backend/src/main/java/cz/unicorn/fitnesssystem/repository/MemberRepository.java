package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByNicknameAndCode(String nickname, String code);

    Optional<Member> findByIdAndDeletedAtIsNull(UUID id);

    // Native: advisory lock until commit, so concurrent code draws for one nickname can't both pass the exists check.
    @Query(value = "SELECT 1 FROM pg_advisory_xact_lock(hashtext(:nickname))", nativeQuery = true)
    int lockNickname(String nickname);
}