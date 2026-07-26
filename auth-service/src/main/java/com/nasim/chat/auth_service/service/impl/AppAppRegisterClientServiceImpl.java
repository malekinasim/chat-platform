package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.Status;
import com.nasim.chat.auth_service.repository.AppRegisterClientRepository;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppAppRegisterClientServiceImpl implements AppRegisterClientService {
    private final AppRegisterClientRepository appRegisterClientRepository;

    public AppAppRegisterClientServiceImpl(AppRegisterClientRepository appRegisterClientRepository) {
        this.appRegisterClientRepository = appRegisterClientRepository;
    }

    @Override
    public void registerClientIfNotExists(String clientId, String audience, String callbackUrl, String onboardingUrl, Status statue) {
        if (appRegisterClientRepository.existsByClientId(clientId)) {
            return;
        }
        AppRegisteredClient client= new AppRegisteredClient();
        client.setClientId(clientId);
        client.setAudience(audience);
        client.setActive(statue);
        client.setCallbackUrl(callbackUrl);
        client.setOnboardingUrl(onboardingUrl);
        appRegisterClientRepository.save(client);
    }

    @Override
    public Optional<AppRegisteredClient> findActiveClient(String clientId) {
        return appRegisterClientRepository.findByClientIdAndStatus(clientId,Status.ACTIVE);
    }
}
