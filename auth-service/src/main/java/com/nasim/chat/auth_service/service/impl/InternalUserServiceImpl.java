package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.service.InternalUserService;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.stereotype.Service;

@Service
public class InternalUserServiceImpl implements InternalUserService {
    private final UserIdentityService userIdentityService;

    public InternalUserServiceImpl(UserIdentityService userIdentityService) {
        this.userIdentityService = userIdentityService;
    }

    @Override
    public InternalUser resolve(String issuer, String externalSubject, String email, String name, String provider) {
        UserIdentity userIdentity= userIdentityService.findOrCreate(issuer,externalSubject,email,name,provider);


        return null;
    }
}
