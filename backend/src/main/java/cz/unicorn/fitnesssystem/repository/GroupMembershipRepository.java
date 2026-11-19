package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.GroupMembership;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GroupMembership> findAllWithLockByGroupIdOrderById(UUID groupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GroupMembership> findWithLockByGroupIdAndMemberId(UUID groupId, UUID memberId);

    @EntityGraph(attributePaths = "group")
    List<GroupMembership> findAllByMemberIdOrderByGroupNameAsc(UUID memberId);

    // Native: ON CONFLICT lets a concurrent second join wait for the first instead of failing on membership_unique.
    @Modifying
    @Query(value = """
            INSERT INTO group_membership (group_id, member_id, joined_at)
            VALUES (:groupId, :memberId, :joinedAt)
            ON CONFLICT (group_id, member_id) DO NOTHING""", nativeQuery = true)
    void insertIfAbsent(UUID groupId, UUID memberId, Instant joinedAt);
}