package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.MembersApi;
import cz.unicorn.fitnesssystem.api.model.ChangeNicknameRequest;
import cz.unicorn.fitnesssystem.api.model.CreateMemberRequest;
import cz.unicorn.fitnesssystem.api.model.MemberIdentity;
import cz.unicorn.fitnesssystem.api.model.MemberProfile;
import cz.unicorn.fitnesssystem.entity.Member;
import cz.unicorn.fitnesssystem.helper.AuthHelper;
import cz.unicorn.fitnesssystem.mapper.MemberMapper;
import cz.unicorn.fitnesssystem.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController implements MembersApi {

    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final AuthHelper authHelper;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<MemberIdentity> createMember(CreateMemberRequest createMemberRequest) {
        Member member = memberService.createMember(memberMapper.toNewMember(createMemberRequest),
                authHelper.registrationProof(), request.getRemoteAddr());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberMapper.toMemberIdentity(member));
    }

    @Override
    public ResponseEntity<MemberProfile> getMyProfile() {
        return ResponseEntity.ok(memberMapper.toMemberProfile(memberService.getProfile(authHelper.currentMember().memberId())));
    }

    @Override
    public ResponseEntity<MemberIdentity> changeNickname(ChangeNicknameRequest changeNicknameRequest) {
        Member member = memberService.changeNickname(authHelper.currentMember().memberId(), changeNicknameRequest.getNickname());
        return ResponseEntity.ok(memberMapper.toMemberIdentity(member));
    }
}