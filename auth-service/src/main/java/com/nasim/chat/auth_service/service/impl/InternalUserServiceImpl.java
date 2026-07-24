package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.service.InternalUserService;
import org.springframework.stereotype.Service;

@Service
public class InternalUserServiceImpl implements InternalUserService {
    @Override
    public InternalUser findOrCreate(String issuer, String externalSubject, String email, String name) {
        return null;
    }
}
