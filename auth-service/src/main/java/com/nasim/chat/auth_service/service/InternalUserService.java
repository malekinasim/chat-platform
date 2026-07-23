package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.InternalUser;

public interface InternalUserService {

    InternalUser findOrCreate(String issuer, String externalSubject, String email, String name);
}
