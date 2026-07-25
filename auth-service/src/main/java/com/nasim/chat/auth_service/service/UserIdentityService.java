package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.UserIdentity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserIdentityService {

    Optional<UserIdentity> findIdentityUserByIssuerAndSubject(String issuer, String externalSubject);
}
