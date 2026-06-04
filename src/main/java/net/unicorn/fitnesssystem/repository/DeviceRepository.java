package net.unicorn.fitnesssystem.repository;

import net.unicorn.fitnesssystem.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUserIdAndPublicKeyHash(Long userId, String publicKeyHash);

    Optional<Device> findByPublicKeyHash(String publicKeyHash);

    boolean existsByPublicKeyHash(String publicKeyHash);
}
