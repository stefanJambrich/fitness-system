package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MemberGroups;
import cz.unicorn.fitnesssystem.dto.MembershipOverview;
import java.util.UUID;

public interface MembershipService {

    /**
     * Finds the group behind an invite code (case-insensitive) together with its trainers' names, owner first.
     * Throws {@code 404 INVITE_INVALID} for an unknown or replaced code or a deleted group.
     *
     * @param inviteCode code from the link, QR code or typed
     * @return the group and its trainers' names
     */
    GroupInvite getInvitePreview(String inviteCode);

    /**
     * Joins the member to the group behind the invite code. An active membership is returned unchanged and a left
     * one is reactivated with its old balance. Throws {@code 404 INVITE_INVALID} for an unknown code and
     * {@code 403 NOT_MEMBER} when the trainer removed the member from the group.
     *
     * @param memberId   joining member
     * @param inviteCode code from the link, QR code or typed
     * @return the active membership with its credits and next training
     */
    MembershipOverview joinGroup(UUID memberId, String inviteCode);

    /**
     * Lists the member's groups with their credits, ordered by name. Active ones carry the next upcoming training;
     * former ones (left, removed or group deleted) are listed separately so their kept balance stays visible.
     *
     * @param memberId member
     * @return active and former memberships
     */
    MemberGroups listMyGroups(UUID memberId);

    /**
     * Ends the member's membership and cancels their sign-ups for trainings that haven't started; sign-ups for
     * trainings already under way stay for the trainer to resolve. The balance is kept, even a negative one, and
     * comes back on rejoin. Throws {@code 403 NOT_MEMBER} without an active membership.
     *
     * @param memberId leaving member
     * @param groupId  group to leave
     * @return the now former membership with its credits
     */
    MembershipOverview leaveGroup(UUID memberId, UUID groupId);
}