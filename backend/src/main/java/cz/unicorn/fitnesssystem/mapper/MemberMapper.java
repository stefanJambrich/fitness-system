package cz.unicorn.fitnesssystem.mapper;

import cz.unicorn.fitnesssystem.api.model.CreateMemberRequest;
import cz.unicorn.fitnesssystem.api.model.KeyAlgorithm;
import cz.unicorn.fitnesssystem.api.model.MemberIdentity;
import cz.unicorn.fitnesssystem.dto.NewMember;
import cz.unicorn.fitnesssystem.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MemberMapper {

    NewMember toNewMember(CreateMemberRequest request);

    @Mapping(target = "memberId", source = "id")
    MemberIdentity toMemberIdentity(Member member);

    default String toAlgorithm(KeyAlgorithm algorithm) {
        return algorithm.getValue();
    }
}