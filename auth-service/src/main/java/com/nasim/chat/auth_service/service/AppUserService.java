package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.AppUser;

public interface AppUserService {

    AppUser findById(Long userId);
}
