package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;

public record GroupMemberOverview(GroupMembership membership, MembershipCredit credit, int upcomingBookings) {
}
