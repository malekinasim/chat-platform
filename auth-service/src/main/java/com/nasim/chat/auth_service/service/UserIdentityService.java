package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.UserIdentity;

public interface UserIdentityService {
    UserIdentity findOrCreate(String issuer, String externalSubject, String email, String name,String provider);
}
