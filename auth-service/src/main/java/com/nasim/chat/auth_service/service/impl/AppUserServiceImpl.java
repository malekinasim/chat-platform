package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.dto.GeneralUserDetails;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<String> findAllActiveIds() {
        return appUserRepository.findAllByActiveTrue().stream()
                .map(appUser -> appUser.getId().toString()).toList();
    }

    @Override
    public List<String> findAllActiveIdsById(List<Long> ids) {
        return appUserRepository.findAllByIdInAndActiveTrue(ids).stream()
                .map(appUser -> appUser.getId().toString()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneralUserDetails> findUserDetails(List<Long> ids) {
        return appUserRepository.findAllByIdInAndActiveTrue(ids).stream()
                .map(user -> new GeneralUserDetails(
                        user.getId().toString(),
                        user.getDisplayName(),
                        user.getAvatarUrl(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getPhoneVerifiedAt(),
                        user.getRoles().stream()
                                .map(Role::getName)
                                .sorted()
                                .collect(Collectors.toCollection(LinkedHashSet::new))
                ))
                .toList();
    }

    @Override
    public boolean activeUserExists(Long userId) {
        return appUserRepository.existsByIdAndActiveTrue(userId);
    }

}
