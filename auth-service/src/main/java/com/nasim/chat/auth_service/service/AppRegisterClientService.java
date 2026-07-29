package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;


import java.util.Optional;

public interface AppRegisterClientService {
    void registerClientIfNotExists(String clientId, String audience, String callback_url, String onboarding_url);
    Optional<AppRegisteredClient> findActiveClient(String clientId);
}
