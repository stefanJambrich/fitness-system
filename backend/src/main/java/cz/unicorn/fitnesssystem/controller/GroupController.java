package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.GroupsApi;
import cz.unicorn.fitnesssystem.api.model.CreateGroupRequest;
import cz.unicorn.fitnesssystem.api.model.TrainerGroup;
import cz.unicorn.fitnesssystem.api.model.UpdateGroupRequest;
import cz.unicorn.fitnesssystem.helper.AuthHelper;
import cz.unicorn.fitnesssystem.mapper.GroupMapper;
import cz.unicorn.fitnesssystem.service.GroupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController implements GroupsApi {

    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final AuthHelper authHelper;

    @Override
    public ResponseEntity<List<TrainerGroup>> listTrainerGroups() {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.ok(groupMapper.toTrainerGroups(groupService.listTrainerGroups(trainerId)));
    }

    @Override
    public ResponseEntity<TrainerGroup> createGroup(CreateGroupRequest createGroupRequest) {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.status(HttpStatus.CREATED).body(groupMapper.toTrainerGroup(
                groupService.createGroup(trainerId, groupMapper.toGroupDetails(createGroupRequest))));
    }

    @Override
    public ResponseEntity<TrainerGroup> updateGroup(UUID groupId, UpdateGroupRequest updateGroupRequest) {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.ok(groupMapper.toTrainerGroup(
                groupService.updateGroup(trainerId, groupId, groupMapper.toGroupDetails(updateGroupRequest))));
    }

    @Override
    public ResponseEntity<Void> deleteGroup(UUID groupId) {
        groupService.deleteGroup(authHelper.currentTrainer().trainerId(), groupId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<TrainerGroup> regenerateInviteCode(UUID groupId) {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.ok(groupMapper.toTrainerGroup(groupService.regenerateInviteCode(trainerId, groupId)));
    }
}