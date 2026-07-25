package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;
import com.nasim.chat.auth_service.model.dto.AuthenticationStatus;
import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.service.InternalUserService;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InternalUserServiceImpl implements InternalUserService {
    private final UserIdentityService userIdentityService;

    public InternalUserServiceImpl(UserIdentityService userIdentityService) {
        this.userIdentityService = userIdentityService;
    }
    @Override
    public AuthenticationResolution resolve(String issuer, String externalSubject, String email, String name, String provider) {

        Optional<UserIdentity> userIdentity= userIdentityService.findIdentityUserByIssuerAndSubject(issuer,externalSubject);
        if(userIdentity.isPresent()){
           AppUser currentUser= userIdentity.get().getAppUser();
            return  new AuthenticationResolution(new InternalUser(currentUser.getId().toString(),
                    currentUser.getRoles().stream().map(Role::getName).toList()),
                    null, AuthenticationStatus.EXISTING_USER);
        }else{
            return  new AuthenticationResolution(null,
                    new PendingRegistration(issuer,externalSubject,email,name,provider, LocalDateTime.now()),
                    AuthenticationStatus.REGISTRATION_REQUIRED);
        }
    }
 
}
