package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.UserIdentity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserIdentityService {
    UserIdentity findOrCreate(String issuer, String externalSubject, String email, String name,String provider);

    @Transactional
    Optional<UserIdentity> findIdentityUserByIssuerAndSubject(String issuer, String externalSubject);
}
