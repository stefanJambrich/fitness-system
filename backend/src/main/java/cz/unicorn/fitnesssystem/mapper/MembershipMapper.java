package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.CreditBalance;
import cz.unicorn.fitnesssystem.api.model.InvitePreview;
import cz.unicorn.fitnesssystem.api.model.MemberGroup;
import cz.unicorn.fitnesssystem.dto.GroupInvite;
import cz.unicorn.fitnesssystem.dto.MembershipWithCredit;
import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MembershipMapper {

    @Mapping(target = ".", source = "group")
    InvitePreview toInvitePreview(GroupInvite invite);

    @Mapping(target = ".", source = "membership.group")
    @Mapping(target = "groupId", source = "membership.group.id")
    MemberGroup toMemberGroup(MembershipWithCredit membership);

    CreditBalance toCreditBalance(MembershipCredit credit);
}