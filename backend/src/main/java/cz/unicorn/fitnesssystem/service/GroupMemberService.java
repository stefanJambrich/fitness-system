package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.GroupMemberOverview;
import cz.unicorn.fitnesssystem.dto.MemberListFilter;
import java.util.List;
import java.util.UUID;

public interface GroupMemberService {

    /**
     * Lists the group's memberships with their members, credits and number of upcoming sign-ups, ordered by
     * nickname. Throws {@code 404 NOT_FOUND} or {@code 403 NOT_GROUP_TRAINER}.
     *
     * @param trainerId trainer of the group
     * @param groupId   group
     * @param filter    active, former or all memberships
     * @return the memberships
     */
    List<GroupMemberOverview> listMembers(UUID trainerId, UUID groupId, MemberListFilter filter);

    /**
     * Removes a member: ends the membership, so the member can't rejoin by the invite link, and cancels their
     * sign-ups for trainings that haven't started. The balance is kept. A member who left is marked as removed;
     * a removed one is left as is. Throws {@code 404 NOT_FOUND}, {@code 403 NOT_GROUP_TRAINER} or
     * {@code 404 MEMBER_NOT_FOUND}.
     *
     * @param trainerId    trainer of the group
     * @param groupId      group
     * @param membershipId membership to end
     */
    void removeMember(UUID trainerId, UUID groupId, UUID membershipId);

    /**
     * Reactivates a removed membership with its old balance; an active one is returned unchanged. Throws
     * {@code 409 NOT_REMOVED} for a member who left by themselves, plus the errors of {@link #removeMember}.
     *
     * @param trainerId    trainer of the group
     * @param groupId      group
     * @param membershipId membership to restore
     * @return the active membership
     */
    GroupMemberOverview restoreMember(UUID trainerId, UUID groupId, UUID membershipId);
}