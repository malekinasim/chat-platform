package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.entity.AppUser;
import jakarta.validation.constraints.Pattern;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {

    AppUser findById(Long userId);

    @Transactional(rollbackFor = Exception.class)
    AppUser createAppUser(String name, String email, String phoneNumber);

    AppUser completeRegistration(@Pattern(regexp = "^\\+[1-9]\\d{7,14}$",
                                                   message = "Phone number must use international format, such as +46701234567") String phoneNumber, PendingRegistration userInfo, String clientId);
}
