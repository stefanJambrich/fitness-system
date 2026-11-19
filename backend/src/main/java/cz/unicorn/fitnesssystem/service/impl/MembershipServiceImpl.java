package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MembershipWithCredit;
import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.MembershipEnd;
import cz.unicorn.fitnesssystem.entity.TrainingGroup;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.GroupMembershipRepository;
import cz.unicorn.fitnesssystem.repository.GroupTrainerRepository;
import cz.unicorn.fitnesssystem.repository.MembershipCreditRepository;
import cz.unicorn.fitnesssystem.repository.TrainingGroupRepository;
import cz.unicorn.fitnesssystem.service.MembershipService;
import java.time.Clock;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final TrainingGroupRepository groupRepository;
    private final GroupTrainerRepository groupTrainerRepository;
    private final GroupMembershipRepository membershipRepository;
    private final MembershipCreditRepository creditRepository;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    public GroupInvite getInvitePreview(String inviteCode) {
        TrainingGroup group = groupByInviteCode(inviteCode);
        return new GroupInvite(group, groupTrainerRepository.findAllByIdGroupIdOrderByRoleAscAddedAtAsc(group.getId())
                .stream()
                .map(groupTrainer -> groupTrainer.getTrainer().getDisplayName())
                .toList());
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public MembershipWithCredit joinGroup(UUID memberId, String inviteCode) {
        TrainingGroup group = groupByInviteCode(inviteCode);
        membershipRepository.insertIfAbsent(group.getId(), memberId, clock.instant());
        GroupMembership membership = membershipRepository.findWithLockByGroupIdAndMemberId(group.getId(), memberId)
                .orElseThrow();

        if (membership.getEndReason() == MembershipEnd.REMOVED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_MEMBER",
                    "The trainer removed you from this group, ask them to add you back");
        }
        if (membership.getEndReason() == MembershipEnd.LEFT) {
            membership.rejoin(clock.instant());
        }
        return new MembershipWithCredit(membership, creditRepository.findById(membership.getId()).orElseThrow());
    }

    private TrainingGroup groupByInviteCode(String inviteCode) {
        return groupRepository.findByInviteCodeAndDeletedAtIsNull(inviteCode.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INVITE_INVALID",
                        "This invite code is not valid, ask the trainer for a new one"));
    }
}