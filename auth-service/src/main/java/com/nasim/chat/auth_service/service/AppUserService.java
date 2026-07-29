package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.entity.AppUser;
import jakarta.validation.constraints.Pattern;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {

    AppUser findById(Long userId);

    @Transactional(rollbackFor = Exception.class)
    AppUser createAppUser(String name, String email, String phoneNumber);

    AppUser completeRegistration( String phoneNumber, PendingRegistration userInfo);
}
