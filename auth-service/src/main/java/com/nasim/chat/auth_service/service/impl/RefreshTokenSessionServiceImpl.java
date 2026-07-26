package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import com.nasim.chat.auth_service.repository.RefreshTokenSessionRepository;
import com.nasim.chat.auth_service.service.AppUserService;
import com.nasim.chat.auth_service.service.RefreshTokenSessionService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenSessionServiceImpl implements RefreshTokenSessionService {
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final AppUserService appUserService;

    public RefreshTokenSessionServiceImpl(RefreshTokenSessionRepository refreshTokenSessionRepository, AppUserService appUserService) {
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.appUserService = appUserService;
    }

    @Override
    public void createAndRevokeRefreshToken(String userId, String tokenId, Instant expiresAt) {
        RefreshTokenSession refreshSession = new RefreshTokenSession();
        refreshSession.setTokenId(tokenId);
        AppUser user=appUserService.findById(Long.getLong(userId));
        refreshSession.setUser(user);
        refreshSession.setExpiresAt(expiresAt);
        refreshTokenSessionRepository.save(refreshSession);
    }
}
