package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;

public interface InternalUserService {

    AuthenticationResolution resolve(
            String issuer,
            String externalSubject,
            String email,
            String displayName,
            boolean emailVerified,
            String provider

    );
}
