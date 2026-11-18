package cz.unicorn.fitnesssystem.service;

import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import java.util.UUID;

public interface GroupAccessService {

    /**
     * Checks that the trainer is a trainer of an existing group (owner or co-trainer). Throws {@code 404 NOT_FOUND}
     * for a missing or deleted group and {@code 403 NOT_GROUP_TRAINER} for a group the trainer doesn't belong to.
     *
     * @param trainerId calling trainer
     * @param groupId   group to act on
     * @return the trainer's link to the group, with the group loaded
     */
    GroupTrainer requireTrainer(UUID trainerId, UUID groupId);

    /**
     * Like {@link #requireTrainer}, but the trainer must be the group's owner, otherwise {@code 403 OWNER_ONLY}.
     *
     * @param trainerId calling trainer
     * @param groupId   group to act on
     * @return the owner's link to the group, with the group loaded
     */
    GroupTrainer requireOwner(UUID trainerId, UUID groupId);
}