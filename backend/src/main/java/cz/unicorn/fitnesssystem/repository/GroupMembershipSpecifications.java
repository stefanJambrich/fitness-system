package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.GroupMembership;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class GroupMembershipSpecifications {

    private GroupMembershipSpecifications() {
    }

    public static Specification<GroupMembership> inGroup(UUID groupId) {
        return (root, query, cb) -> cb.equal(root.get("group").get("id"), groupId);
    }

    public static Specification<GroupMembership> isActive() {
        return (root, query, cb) -> cb.isNull(root.get("leftAt"));
    }

    public static Specification<GroupMembership> isFormer() {
        return (root, query, cb) -> cb.isNotNull(root.get("leftAt"));
    }
}