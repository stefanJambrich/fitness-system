package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.dto.GroupDetails;
import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import java.util.List;
import java.util.UUID;

public interface GroupService {

    /**
     * Lists the groups the trainer owns or co-trains, without deleted ones, ordered by name.
     *
     * @param trainerId trainer
     * @return the groups, each with the trainer's role in it
     */
    List<GroupTrainer> listTrainerGroups(UUID trainerId);

    /**
     * Creates a group with a new invite code and makes the trainer its owner. A missing time zone or cancel-hours
     * value gets the configured default. Throws {@code 400 VALIDATION} for a blank name or an unknown time zone.
     *
     * @param trainerId future owner
     * @param details   name, description, time zone and cancel hours
     * @return the new group with the owner role
     */
    GroupTrainer createGroup(UUID trainerId, GroupDetails details);

    /**
     * Changes the given fields of a group; null fields stay as they are and a blank description removes it.
     * Throws {@code 404 NOT_FOUND}, {@code 403 NOT_GROUP_TRAINER} or {@code 400 VALIDATION}.
     *
     * @param trainerId trainer of the group
     * @param groupId   group to change
     * @param details   fields to change
     * @return the changed group with the trainer's role
     */
    GroupTrainer updateGroup(UUID trainerId, UUID groupId, GroupDetails details);

    /**
     * Soft-deletes a group: cancels its future trainings together with their bookings, ends its series and hides it
     * from trainers. Members keep their memberships and balances. Throws {@code 404 NOT_FOUND},
     * {@code 403 NOT_GROUP_TRAINER} or {@code 403 OWNER_ONLY}.
     *
     * @param trainerId owner of the group
     * @param groupId   group to delete
     */
    void deleteGroup(UUID trainerId, UUID groupId);

    /**
     * Replaces the group's invite code, so the old link stops working; existing members stay. Throws
     * {@code 404 NOT_FOUND}, {@code 403 NOT_GROUP_TRAINER} or {@code 403 OWNER_ONLY}.
     *
     * @param trainerId owner of the group
     * @param groupId   group whose code to replace
     * @return the group with the new code
     */
    GroupTrainer regenerateInviteCode(UUID trainerId, UUID groupId);
}