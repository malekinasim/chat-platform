package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.Status;

import java.util.Optional;

public interface AppRegisterClientService {
    void registerClientIfNotExists(String clinetId, String audience, String callback_url, String onboarding_url, Status statue);
    Optional<AppRegisteredClient> findActiveClient(String clientId);
}
