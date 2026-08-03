package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.model.entity.UserIdentity;
import com.nasim.chat.auth_service.repository.AppUserRepository;
import com.nasim.chat.auth_service.service.AppUserService;
import com.nasim.chat.auth_service.service.RoleService;
import com.nasim.chat.auth_service.service.UserIdentityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final RoleService roleService;
    private final UserIdentityService userIdentityService;

    public AppUserServiceImpl(AppUserRepository appUserRepository, RoleService roleService, UserIdentityService userIdentityService) {
        this.appUserRepository = appUserRepository;
        this.roleService = roleService;
        this.userIdentityService = userIdentityService;
    }

    @Override
    public AppUser findById(Long userId) {
        return appUserRepository.findById(userId).orElseThrow(
                ()->new CustomException("can noot find user","IVALID_USER_ID")
        );
    }
   @Transactional(rollbackFor = Exception.class)
   @Override
   public AppUser createAppUser(String name, String email, String phoneNumber){
        AppUser newUser = new AppUser();
        newUser.setPhoneVerifiedAt(Instant.now()); // TODO: Replace temporary trusted-phone assumption with real OTP verification.
        newUser.setPhoneNumber(phoneNumber);
        newUser.setEmail(email);
        newUser.setDisplayName(name);
        Role role= roleService.createRoleIfNotExists("USER",null);
        newUser.setRoles(Set.of(role));
        return appUserRepository.save(newUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppUser completeRegistration(String phoneNumber, PendingRegistration userInfo) {
      AppUser appUser=  appUserRepository.findByPhoneNumber(phoneNumber).orElseGet(
              ()-> this.createAppUser(userInfo.displayName(),userInfo.email(),phoneNumber)
      );

        userIdentityService.createUserIdentity(
                userInfo.issuer(),
                userInfo.subject(),
                userInfo.provider(),
                userInfo.email(),
                appUser);
        return appUser;
    }

    @Override
    public List<Long> findAll() {
        return appUserRepository.findAllByActiveTrue().stream()
                .map(AppUser::getId).toList();
    }

    @Override
    public List<Long> findAllById(List<Long> ids) {
        return appUserRepository.findAllByIdAndActiveTrue(ids).stream()
                .map(AppUser::getId).toList();
    }

    @Override
    public boolean existsByUsername(Long id) {
        return appUserRepository.findById(id).isPresent();
    }
}
