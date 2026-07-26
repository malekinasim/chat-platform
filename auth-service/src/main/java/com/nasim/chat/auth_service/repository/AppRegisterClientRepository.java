package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRegisterClientRepository extends JpaRepository<AppRegisteredClient,Long> {
    Optional<AppRegisteredClient> findByClientIdAndActiveTrue(String clientId);
}
