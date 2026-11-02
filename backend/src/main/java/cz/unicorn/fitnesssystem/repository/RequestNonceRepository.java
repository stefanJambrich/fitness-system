package cz.unicorn.fitnesssystem.repository;

import cz.unicorn.fitnesssystem.entity.RequestNonce;
import cz.unicorn.fitnesssystem.entity.RequestNonceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestNonceRepository extends JpaRepository<RequestNonce, RequestNonceId> {
}