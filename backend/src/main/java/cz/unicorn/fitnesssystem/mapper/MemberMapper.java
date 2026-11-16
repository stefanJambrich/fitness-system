package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.CreateMemberRequest;
import cz.unicorn.fitnesssystem.api.model.KeyAlgorithm;
import cz.unicorn.fitnesssystem.api.model.MemberIdentity;
import cz.unicorn.fitnesssystem.api.model.MemberProfile;
import cz.unicorn.fitnesssystem.dto.MemberWithDeviceCount;
import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface MemberMapper {

    NewMember toNewMember(CreateMemberRequest request);

    @Mapping(target = "memberId", source = "id")
    MemberIdentity toMemberIdentity(Member member);

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "nickname", source = "member.nickname")
    @Mapping(target = "code", source = "member.code")
    @Mapping(target = "recoveryEmail", source = "member.recoveryEmail", qualifiedByName = "maskEmail")
    MemberProfile toMemberProfile(MemberWithDeviceCount profile);

    default String toAlgorithm(KeyAlgorithm algorithm) {
        return algorithm.getValue();
    }

    @Named("maskEmail")
    default String maskEmail(String email) {
        return email == null ? null : email.charAt(0) + "***" + email.substring(email.lastIndexOf('@'));
    }
}