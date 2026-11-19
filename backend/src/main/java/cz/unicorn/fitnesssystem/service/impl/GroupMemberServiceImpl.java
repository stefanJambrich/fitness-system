package cz.unicorn.fitnesssystem.service.impl;

import static cz.unicorn.fitnesssystem.repository.GroupMembershipSpecifications.inGroup;
import static cz.unicorn.fitnesssystem.repository.GroupMembershipSpecifications.isActive;
import static cz.unicorn.fitnesssystem.repository.GroupMembershipSpecifications.isFormer;

import cz.unicorn.fitnesssystem.annotation.ReadOnlyTransaction;
import cz.unicorn.fitnesssystem.annotation.ReadWriteTransaction;
import cz.unicorn.fitnesssystem.dto.BookingCount;
import cz.unicorn.fitnesssystem.dto.GroupMemberOverview;
import cz.unicorn.fitnesssystem.dto.MemberListFilter;
import cz.unicorn.fitnesssystem.entity.BookingStatus;
import cz.unicorn.fitnesssystem.entity.CancelReason;
import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.GroupTrainer;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import cz.unicorn.fitnesssystem.entity.MembershipEnd;
import cz.unicorn.fitnesssystem.exception.ApiException;
import cz.unicorn.fitnesssystem.repository.BookingRepository;
import cz.unicorn.fitnesssystem.repository.GroupMembershipRepository;
import cz.unicorn.fitnesssystem.repository.MembershipCreditRepository;
import cz.unicorn.fitnesssystem.service.GroupAccessService;
import cz.unicorn.fitnesssystem.service.GroupMemberService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private static final Sort BY_IDENTIFIER = Sort.by("member.nickname", "member.code");

    private final GroupAccessService groupAccessService;
    private final GroupMembershipRepository membershipRepository;
    private final MembershipCreditRepository creditRepository;
    private final BookingRepository bookingRepository;
    private final Clock clock;

    /** {@inheritDoc} */
    @Override
    public List<GroupMemberOverview> listMembers(UUID trainerId, UUID groupId, MemberListFilter filter) {
        groupAccessService.requireTrainer(trainerId, groupId);
        Specification<GroupMembership> spec = switch (filter) {
            case ACTIVE -> inGroup(groupId).and(isActive());
            case FORMER -> inGroup(groupId).and(isFormer());
            case ALL -> inGroup(groupId);
        };
        return loadOverviews(membershipRepository.findAll(spec, BY_IDENTIFIER));
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public void removeMember(UUID trainerId, UUID groupId, UUID membershipId) {
        GroupTrainer access = groupAccessService.requireTrainer(trainerId, groupId);
        GroupMembership membership = lockedMembership(groupId, membershipId);
        if (membership.getEndReason() == MembershipEnd.REMOVED) {
            return;
        }
        Instant now = clock.instant();
        bookingRepository.findAllByMembershipIdAndStatusAndTrainingStartsAtAfter(
                        membershipId, BookingStatus.BOOKED, now)
                .forEach(booking -> booking.cancel(now, CancelReason.MEMBER_REMOVED));
        membership.remove(now, access.getTrainer());
    }

    /** {@inheritDoc} */
    @Override
    @ReadWriteTransaction
    public GroupMemberOverview restoreMember(UUID trainerId, UUID groupId, UUID membershipId) {
        groupAccessService.requireTrainer(trainerId, groupId);
        GroupMembership membership = lockedMembership(groupId, membershipId);
        if (membership.getEndReason() == MembershipEnd.LEFT) {
            throw new ApiException(HttpStatus.CONFLICT, "NOT_REMOVED",
                    "The member left by themselves and can rejoin with the invite link");
        }
        if (membership.getEndReason() == MembershipEnd.REMOVED) {
            membership.rejoin(clock.instant());
        }
        return loadOverviews(List.of(membership)).getFirst();
    }

    private GroupMembership lockedMembership(UUID groupId, UUID membershipId) {
        return membershipRepository.findWithLockByIdAndGroupId(membershipId, groupId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND",
                        "This member is not in the group"));
    }

    private List<GroupMemberOverview> loadOverviews(List<GroupMembership> memberships) {
        if (memberships.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = memberships.stream().map(GroupMembership::getId).toList();
        Map<UUID, MembershipCredit> credits = creditRepository.findAllByIdAsMap(ids);
        Map<UUID, Long> upcoming = bookingRepository
                .countByMembershipIdInAndStatusAndTrainingStartsAtAfter(ids, BookingStatus.BOOKED, clock.instant())
                .stream()
                .collect(Collectors.toMap(BookingCount::membershipId, BookingCount::count));
        return memberships.stream()
                .map(membership -> new GroupMemberOverview(membership, credits.get(membership.getId()),
                        upcoming.getOrDefault(membership.getId(), 0L).intValue()))
                .toList();
    }
}