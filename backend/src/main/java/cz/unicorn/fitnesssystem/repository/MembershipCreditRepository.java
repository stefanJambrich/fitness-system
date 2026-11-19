package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipCreditRepository extends JpaRepository<MembershipCredit, UUID> {

    default Map<UUID, MembershipCredit> findAllByIdAsMap(Collection<UUID> membershipIds) {
        return findAllById(membershipIds).stream()
                .collect(Collectors.toMap(MembershipCredit::getMembershipId, Function.identity()));
    }
}