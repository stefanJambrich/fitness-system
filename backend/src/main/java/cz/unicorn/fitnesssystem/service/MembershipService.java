package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MembershipWithCredit;
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
     * @return the active membership with its credits
     */
    MembershipWithCredit joinGroup(UUID memberId, String inviteCode);
}