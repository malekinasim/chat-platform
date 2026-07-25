package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIdentityRepository extends JpaRepository<UserIdentity,Long> {
    boolean existsByIssuerAndExternalSubject(String issuer, String externalSubject);
}
