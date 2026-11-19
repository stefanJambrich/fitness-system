package cz.unicorn.fitnesssystem.service.impl;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MemberGroups;
import cz.unicorn.fitnesssystem.dto.MembershipOverview;
import cz.unicorn.fitnesssystem.entity.CancelReason;
import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import cz.unicorn.fitnesssystem.entity.MembershipEnd;
import cz.unicorn.fitnesssystem.entity.Training;
import cz.unicorn.fitnesssystem.entity.TrainingGroup;
import cz.unicorn.fitnesssystem.entity.TrainingStatus;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.BookingRepository;
import cz.unicorn.fitnesssystem.repository.GroupMembershipRepository;
import cz.unicorn.fitnesssystem.repository.GroupTrainerRepository;
import cz.unicorn.fitnesssystem.repository.MembershipCreditRepository;
import cz.unicorn.fitnesssystem.repository.TrainingGroupRepository;
import cz.unicorn.fitnesssystem.repository.TrainingRepository;
import cz.unicorn.fitnesssystem.service.BookingService;
import cz.unicorn.fitnesssystem.service.GroupAccessService;
import cz.unicorn.fitnesssystem.service.MembershipService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final TrainingRepository trainingRepository;
    private final BookingRepository bookingRepository;
    private final GroupAccessService groupAccessService;
    private final BookingService bookingService;
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
    public MembershipOverview joinGroup(UUID memberId, String inviteCode) {
        TrainingGroup group = groupByInviteCode(inviteCode);
        Instant now = clock.instant();
        membershipRepository.insertIfAbsent(group.getId(), memberId, now);
        GroupMembership membership = membershipRepository.findWithLockByGroupIdAndMemberId(group.getId(), memberId)
                .orElseThrow();

        if (membership.getEndReason() == MembershipEnd.REMOVED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_MEMBER",
                    "The trainer removed you from this group, ask them to add you back");
        }
        if (membership.getEndReason() == MembershipEnd.LEFT) {
            membership.rejoin(now);
        }
        return loadOverviews(List.of(membership), now).getFirst();
    }

    /** {@inheritDoc} */
    @Override
    public MemberGroups listMyGroups(UUID memberId) {
        Map<Boolean, List<MembershipOverview>> byActive = loadOverviews(
                membershipRepository.findAllByMemberIdOrderByGroupNameAsc(memberId), clock.instant())
                .stream()
                .collect(Collectors.partitioningBy(overview -> isActive(overview.membership())));
        return new MemberGroups(byActive.get(true), byActive.get(false));
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public MembershipOverview leaveGroup(UUID memberId, UUID groupId) {
        GroupMembership membership = groupAccessService.requireMemberWithLock(memberId, groupId);
        Instant now = clock.instant();
        bookingService.cancelUpcomingBookings(membership.getId(), CancelReason.MEMBER_LEFT, now);
        membership.leave(now);
        return loadOverviews(List.of(membership), now).getFirst();
    }

    private List<MembershipOverview> loadOverviews(List<GroupMembership> memberships, Instant now) {
        if (memberships.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = memberships.stream().map(GroupMembership::getId).toList();
        Map<UUID, MembershipCredit> credits = creditRepository.findAllByIdAsMap(ids);
        Map<UUID, Long> upcoming = bookingRepository.countUpcomingByMembershipId(ids, now);
        return memberships.stream()
                .map(membership -> new MembershipOverview(membership, credits.get(membership.getId()),
                        upcoming.getOrDefault(membership.getId(), 0L).intValue(),
                        isActive(membership) ? nextTraining(membership.getGroup(), now) : null))
                .toList();
    }

    private static boolean isActive(GroupMembership membership) {
        return membership.getLeftAt() == null && membership.getGroup().getDeletedAt() == null;
    }

    private Training nextTraining(TrainingGroup group, Instant now) {
        return trainingRepository.findFirstByGroupIdAndStatusAndStartsAtAfterOrderByStartsAtAsc(
                group.getId(), TrainingStatus.SCHEDULED, now).orElse(null);
    }

    private TrainingGroup groupByInviteCode(String inviteCode) {
        return groupRepository.findByInviteCodeAndDeletedAtIsNull(inviteCode.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "INVITE_INVALID",
                        "This invite code is not valid, ask the trainer for a new one"));
    }
}