package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.repository.UserIdentityRepository;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UserIdentityServiceImpl implements UserIdentityService {
    private final UserIdentityRepository userIdentityRepository;

    public UserIdentityServiceImpl(UserIdentityRepository userIdentityRepository) {
        this.userIdentityRepository = userIdentityRepository;
    }

    @Override
    public Optional<UserIdentity> findIdentityUserByIssuerAndSubject(String issuer, String externalSubject) {
        return userIdentityRepository.findByIssuerAndSubject(issuer,externalSubject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserIdentity createUserIdentity(String issuer, String subject, String provider, String email, AppUser appUser) {
        if (userIdentityRepository.existsByIssuerAndSubject(
                issuer,
                subject
        )) {
            throw new CustomException(
                    "This identity is already linked",
                    "IDENTITY_ALREADY_LINKED"
            );
        }
        UserIdentity userIdentity=new UserIdentity();
       userIdentity.setIssuer(issuer);
       userIdentity.setSubject(subject);
       userIdentity.setProvider(provider);
       userIdentity.setProviderEmail(email);
       userIdentity.setAppUser(appUser);
        return userIdentityRepository.save(userIdentity);
    }
}
