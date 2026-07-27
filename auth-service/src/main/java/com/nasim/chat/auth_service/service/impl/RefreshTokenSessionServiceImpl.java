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
    @Transactional
    public Optional<RefreshTokenSession> findByTokenHash(String tokenHash) {
        return refreshTokenSessionRepository.findNonExpiredByHashToken(tokenHash);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAndRevokeRefreshToken(String userId, String hashToken, String clientId, RefreshTokenSession oldRefreshToken, Instant expiresAt) {
        if (oldRefreshToken == null) {
            Optional<RefreshTokenSession> preRefreshTokenSession = refreshTokenSessionRepository.
                    findCurrentRefreshTokenByUserIdAndClientId(Long.parseLong(userId), clientId);
            oldRefreshToken = preRefreshTokenSession.orElse(null);
        }

        RefreshTokenSession refreshSession = new RefreshTokenSession();
        refreshSession.setTokenHash(hashToken);
        AppUser user = appUserService.findById(Long.parseLong(userId));
        refreshSession.setUser(user);
        refreshSession.setExpiresAt(expiresAt);
        AppRegisteredClient client = appRegisterClientService.findActiveClient(clientId)
                .orElseThrow(
                        () -> new CustomException("invalid client id ", "INVALID_CLIENT_ID")
                );

        refreshSession.setClient(client);
        refreshTokenSessionRepository.save(refreshSession);

        if (oldRefreshToken != null) {
            this.revokeRefreshToken(oldRefreshToken, refreshSession);
        }

    }

    @Override
    public void revokeRefreshToken(String hashToken) {
        RefreshTokenSession oldRefreshToken = this.findByTokenHash(hashToken)
                .orElseThrow(
                        () -> new CustomException("can nor find valid refreshToken",
                                "INVALID_TOKEN_HASH")
                );
        revokeRefreshToken(oldRefreshToken, null);
    }

    private void revokeRefreshToken(RefreshTokenSession oldRefreshToken, RefreshTokenSession newRefreshToken) {
        if (newRefreshToken != null)
            oldRefreshToken.setReplacedByToken(newRefreshToken);
        oldRefreshToken.setRevokedAt(Instant.now());
        refreshTokenSessionRepository.save(oldRefreshToken);
    }
}
