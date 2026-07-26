package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRegisterClientRepository extends JpaRepository<AppRegisteredClient,Integer> {
    Optional<AppRegisteredClient> findByClientIdAndStatus(String clientId,Status active);
    boolean existsByClientId(String clientId);
}
