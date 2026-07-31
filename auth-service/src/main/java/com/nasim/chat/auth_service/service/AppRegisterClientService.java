package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;


import java.util.Optional;

public interface AppRegisterClientService {
    void registerClientIfNotExists(String clientId, String audience, String callbackUrl, String onboardingUrl,String originUrl);
    Optional<AppRegisteredClient> findActiveClient(String clientId);
    boolean isAllowedOrigin(String requestOrigin);
}
