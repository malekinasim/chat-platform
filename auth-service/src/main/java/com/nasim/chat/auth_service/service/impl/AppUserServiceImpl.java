package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.repository.AppUserRepository;
import com.nasim.chat.auth_service.service.AppUserService;
import org.springframework.stereotype.Service;

@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AppUser findById(Long userId) {
        return appUserRepository.findById(userId).orElseThrow(
                ()->new CustomException("can noot find user","IVALID_USER_ID")
        );
    }
}
