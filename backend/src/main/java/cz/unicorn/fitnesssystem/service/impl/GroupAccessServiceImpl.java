package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import cz.unicorn.fitnesssystem.entity.GroupTrainerId;
import cz.unicorn.fitnesssystem.entity.GroupTrainerRole;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.GroupTrainerRepository;
import cz.unicorn.fitnesssystem.repository.TrainingGroupRepository;
import cz.unicorn.fitnesssystem.service.GroupAccessService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class GroupAccessServiceImpl implements GroupAccessService {

    private final GroupTrainerRepository groupTrainerRepository;
    private final TrainingGroupRepository groupRepository;

    /** {@inheritDoc} */
    @Override
    public GroupTrainer requireTrainer(UUID trainerId, UUID groupId) {
        return groupTrainerRepository.findWithGroupById(new GroupTrainerId(groupId, trainerId))
                .filter(access -> access.getGroup().getDeletedAt() == null)
                .orElseThrow(() -> groupRepository.existsByIdAndDeletedAtIsNull(groupId)
                        ? new ApiException(HttpStatus.FORBIDDEN, "NOT_GROUP_TRAINER", "You are not a trainer of this group")
                        : new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Group not found"));
    }

    /** {@inheritDoc} */
    @Override
    public GroupTrainer requireOwner(UUID trainerId, UUID groupId) {
        GroupTrainer access = requireTrainer(trainerId, groupId);
        if (access.getRole() != GroupTrainerRole.OWNER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "OWNER_ONLY", "Only the group owner can do this");
        }
        return access;
    }
}