package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.MembershipCredit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipCreditRepository extends JpaRepository<MembershipCredit, UUID> {
}