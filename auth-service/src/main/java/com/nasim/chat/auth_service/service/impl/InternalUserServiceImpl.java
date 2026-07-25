package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;
import com.nasim.chat.auth_service.model.dto.AuthenticationStatus;
import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.model.entity.Status;
import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.service.InternalUserService;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.DisabledException;
@Service
public class InternalUserServiceImpl implements InternalUserService {
    private final UserIdentityService userIdentityService;


    public static final int ONBOARDING_SESSION_TTL_SECONDS = 300;

    public InternalUserServiceImpl(UserIdentityService userIdentityService) {
        this.userIdentityService = userIdentityService;
    }
    @Override
    @Transactional(readOnly = true)
    public AuthenticationResolution resolve(String issuer, String externalSubject, String email, String name,boolean emailVerified, String provider) {

        Optional<UserIdentity> userIdentity= userIdentityService.findIdentityUserByIssuerAndSubject(issuer,externalSubject);
        if(userIdentity.isPresent()){
           AppUser currentUser= userIdentity.get().getAppUser();
            if (currentUser.getStatus() != Status.ACTIVE)
                throw new DisabledException("the current user is not Active");
            return  new AuthenticationResolution(new InternalUser(currentUser.getId().toString(),
                    currentUser.getRoles().stream().map(Role::getName).toList()),
                    null, AuthenticationStatus.EXISTING_USER);
        }else{
            PendingRegistration pendingRegistration =
                    new PendingRegistration(
                            issuer,
                            externalSubject,
                            provider,
                            email,
                            emailVerified,
                            name,
                            Instant.now().plusSeconds(ONBOARDING_SESSION_TTL_SECONDS)
                    );

            return new AuthenticationResolution(
                    null,
                    pendingRegistration,
                    AuthenticationStatus.REGISTRATION_REQUIRED
            );
        }
    }
 
}
