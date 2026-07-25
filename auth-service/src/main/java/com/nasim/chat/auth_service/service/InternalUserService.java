package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;
import com.nasim.chat.auth_service.model.dto.InternalUser;

public interface InternalUserService {

    AuthenticationResolution resolve(String issuer, String externalSubject, String email,
                                     String name,
                                     boolean emailVerified,
                                     String provider);
}
