package cz.unicorn.fitnesssystem.dto;

import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import cz.unicorn.fitnesssystem.entity.Training;

public record MembershipOverview(GroupMembership membership, MembershipCredit credit, int upcomingBookings,
                                 Training nextTraining) {
}
