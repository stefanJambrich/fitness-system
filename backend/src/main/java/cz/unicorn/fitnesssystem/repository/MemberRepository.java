package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.Member;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByNicknameAndCode(String nickname, String code);
}