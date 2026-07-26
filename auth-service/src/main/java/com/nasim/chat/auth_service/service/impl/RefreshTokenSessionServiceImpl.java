package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import com.nasim.chat.auth_service.repository.RefreshTokenSessionRepository;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import com.nasim.chat.auth_service.service.AppUserService;
import com.nasim.chat.auth_service.service.RefreshTokenSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenSessionServiceImpl implements RefreshTokenSessionService {
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final AppUserService appUserService;
    private final AppRegisterClientService appRegisterClientService;

    public RefreshTokenSessionServiceImpl(RefreshTokenSessionRepository refreshTokenSessionRepository, AppUserService appUserService, AppRegisterClientService appRegisterClientService) {
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.appUserService = appUserService;
        this.appRegisterClientService = appRegisterClientService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAndRevokeRefreshToken(String userId, String tokenId,String clientId, Instant expiresAt) {
        Optional<RefreshTokenSession> preRefreshTokenSession= refreshTokenSessionRepository.
                findCurrentRefreshTokenByUserIdAndClientId(Long.getLong(userId),clientId);

        RefreshTokenSession refreshSession = new RefreshTokenSession();
        refreshSession.setTokenId(tokenId);
        AppUser user=appUserService.findById(Long.getLong(userId));
        refreshSession.setUser(user);
        refreshSession.setExpiresAt(expiresAt);
        AppRegisteredClient client= appRegisterClientService.findActiveClient(clientId)
                .orElseThrow(
                        ()-> new CustomException("invalid client id ","INVALID_CLIENT_ID")
                );

        refreshSession.setClient(client);
        refreshTokenSessionRepository.save(refreshSession);

        if(preRefreshTokenSession.isPresent()){
            RefreshTokenSession preRefreshSession=preRefreshTokenSession.get();
            preRefreshSession.setReplacedByToken(refreshSession);
            preRefreshSession.setRevokedAt(Instant.now());
            refreshTokenSessionRepository.save(preRefreshSession);
        }

    }
}
