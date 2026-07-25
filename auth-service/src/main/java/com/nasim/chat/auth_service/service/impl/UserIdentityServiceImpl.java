package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.repository.UserIdentityRepository;
import com.nasim.chat.auth_service.service.AppUserService;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class UserIdentityServiceImpl implements UserIdentityService {
    private final UserIdentityRepository userIdentityRepository;
    private final AppUserService appUserService;
    public UserIdentityServiceImpl(UserIdentityRepository userIdentityRepository, AppUserService appUserService) {
        this.userIdentityRepository = userIdentityRepository;
        this.appUserService = appUserService;
    }


    @Override
    @Transactional
    public UserIdentity findOrCreate(String issuer, String externalSubject, String email, String name,String provider) {
       if (!userIdentityRepository.existsByIssuerAndSubject(issuer,externalSubject)) {
            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setIssuer(issuer);
            userIdentity.setProvider(provider);
            userIdentity.setSubject(externalSubject);
            userIdentity.setProviderEmail(email);
           /*AppUser appUser=AppUserService.findAppUserBy
            if()
            userIdentity.setAppUser();*/
            userIdentityRepository.save(userIdentity);
        }
       return null;
    }
    @Transactional
    @Override
    public Optional<UserIdentity> findIdentityUserByIssuerAndSubject(String issuer, String externalSubject) {
        return this.findIdentityUserByIssuerAndSubject(issuer,externalSubject);
    }
}
