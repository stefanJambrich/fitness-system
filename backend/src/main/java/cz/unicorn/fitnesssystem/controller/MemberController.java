package cz.unicorn.fitnesssystem.controller;

import cz.unicorn.fitnesssystem.api.MembersApi;
import cz.unicorn.fitnesssystem.api.model.CreateMemberRequest;
import cz.unicorn.fitnesssystem.api.model.MemberIdentity;
import cz.unicorn.fitnesssystem.config.AppProperties;
import cz.unicorn.fitnesssystem.dto.SignedRequest;
import cz.unicorn.fitnesssystem.entity.Member;
import cz.unicorn.fitnesssystem.mapper.MemberMapper;
import cz.unicorn.fitnesssystem.ratelimit.RateLimiter;
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
    private final RateLimiter rateLimiter;
    private final AppProperties properties;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<MemberIdentity> createMember(CreateMemberRequest createMemberRequest) {
        AppProperties.Member limits = properties.member();
        rateLimiter.acquire("create-member:" + request.getRemoteAddr(),
                limits.createLimitPerIp(), limits.createLimitWindow());

        SignedRequest proof = (SignedRequest) request.getAttribute(SignedRequest.ATTRIBUTE);
        Member member = memberService.createMember(memberMapper.toNewMember(createMemberRequest), proof);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberMapper.toMemberIdentity(member));
    }
}