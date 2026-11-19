package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.CreditBalance;
import cz.unicorn.fitnesssystem.api.model.InvitePreview;
import cz.unicorn.fitnesssystem.api.model.MemberGroup;
import cz.unicorn.fitnesssystem.api.model.MembershipEndReason;
import cz.unicorn.fitnesssystem.api.model.MyGroups;
import cz.unicorn.fitnesssystem.api.model.TrainingSummary;
import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MemberGroups;
import cz.unicorn.fitnesssystem.dto.MembershipOverview;
import cz.unicorn.fitnesssystem.entity.GroupMembership;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import cz.unicorn.fitnesssystem.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TimeMapper.class)
public interface MembershipMapper {

    @Mapping(target = ".", source = "group")
    InvitePreview toInvitePreview(GroupInvite invite);

    MyGroups toMyGroups(MemberGroups groups);

    @Mapping(target = ".", source = "membership.group")
    @Mapping(target = "id", source = "membership.group.id")
    @Mapping(target = "endReason", source = "membership")
    MemberGroup toMemberGroup(MembershipOverview overview);

    CreditBalance toCreditBalance(MembershipCredit credit);

    TrainingSummary toTrainingSummary(Training training);

    /** A deleted group outranks the membership's own end, because neither can then be rejoined. */
    default MembershipEndReason toEndReason(GroupMembership membership) {
        if (membership.getGroup().getDeletedAt() != null) {
            return MembershipEndReason.GROUP_DELETED;
        }
        if (membership.getEndReason() == null) {
            return null;
        }
        return switch (membership.getEndReason()) {
            case LEFT -> MembershipEndReason.LEFT;
            case REMOVED -> MembershipEndReason.REMOVED;
        };
    }
}