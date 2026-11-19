package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.MemberGroupsApi;
import cz.unicorn.fitnesssystem.api.model.InvitePreview;
import cz.unicorn.fitnesssystem.api.model.MemberGroup;
import cz.unicorn.fitnesssystem.helper.AuthHelper;
import cz.unicorn.fitnesssystem.mapper.MembershipMapper;
import cz.unicorn.fitnesssystem.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberGroupController implements MemberGroupsApi {

    private final MembershipService membershipService;
    private final MembershipMapper membershipMapper;
    private final AuthHelper authHelper;

    @Override
    public ResponseEntity<InvitePreview> getInvitePreview(String code) {
        return ResponseEntity.ok(membershipMapper.toInvitePreview(membershipService.getInvitePreview(code)));
    }

    @Override
    public ResponseEntity<MemberGroup> joinGroup(String code) {
        return ResponseEntity.ok(membershipMapper.toMemberGroup(
                membershipService.joinGroup(authHelper.currentMember().memberId(), code)));
    }
}