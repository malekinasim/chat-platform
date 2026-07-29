package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.UserIdentity;

import java.util.Optional;

public interface UserIdentityService {

    Optional<UserIdentity> findIdentityUserByIssuerAndSubject(String issuer, String externalSubject);

    UserIdentity createUserIDentity(String issuer, String subject, String provider, String email, AppUser appUser);
}
