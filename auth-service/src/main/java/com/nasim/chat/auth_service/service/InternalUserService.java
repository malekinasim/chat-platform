package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.InternalUser;

public interface InternalUserService {

    InternalUser resolve(String issuer, String externalSubject, String email, String name, String provider);
}
