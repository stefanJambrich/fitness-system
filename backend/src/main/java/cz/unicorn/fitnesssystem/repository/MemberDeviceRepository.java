package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.MemberDevice;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDeviceRepository extends JpaRepository<MemberDevice, UUID> {

    boolean existsByPublicKeyHash(byte[] publicKeyHash);
}