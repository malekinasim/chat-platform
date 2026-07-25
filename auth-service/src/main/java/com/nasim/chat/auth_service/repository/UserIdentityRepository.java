package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity,Long> {
    boolean existsByIssuerAndSubject(String issuer, String externalSubject);
    Optional<UserIdentity> findByIssuerAndSubject(String issuer, String subject);
}
