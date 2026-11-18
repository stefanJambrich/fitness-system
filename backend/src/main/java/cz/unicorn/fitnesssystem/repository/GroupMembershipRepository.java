package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.GroupMembership;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GroupMembership> findAllWithLockByGroupIdOrderById(UUID groupId);
}