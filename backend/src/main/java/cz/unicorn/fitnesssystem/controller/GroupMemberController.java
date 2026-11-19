package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.GroupMembersApi;
import cz.unicorn.fitnesssystem.api.model.GroupMember;
import cz.unicorn.fitnesssystem.api.model.MembershipFilter;
import cz.unicorn.fitnesssystem.helper.AuthHelper;
import cz.unicorn.fitnesssystem.mapper.MembershipMapper;
import cz.unicorn.fitnesssystem.service.GroupMemberService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupMemberController implements GroupMembersApi {

    private final GroupMemberService groupMemberService;
    private final MembershipMapper membershipMapper;
    private final AuthHelper authHelper;

    @Override
    public ResponseEntity<List<GroupMember>> listGroupMembers(UUID groupId, MembershipFilter status) {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.ok(membershipMapper.toGroupMembers(
                groupMemberService.listMembers(trainerId, groupId, membershipMapper.toMemberListFilter(status))));
    }

    @Override
    public ResponseEntity<Void> removeGroupMember(UUID groupId, UUID membershipId) {
        groupMemberService.removeMember(authHelper.currentTrainer().trainerId(), groupId, membershipId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<GroupMember> restoreGroupMember(UUID groupId, UUID membershipId) {
        UUID trainerId = authHelper.currentTrainer().trainerId();
        return ResponseEntity.ok(membershipMapper.toGroupMember(
                groupMemberService.restoreMember(trainerId, groupId, membershipId)));
    }
}
