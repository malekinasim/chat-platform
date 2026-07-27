package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenSessionService {

    Optional<RefreshTokenSession> findByTokenHash(String tokenId);

    void createAndRevokeRefreshToken(String userId, String hashToken, String clientId, Instant expiresAt);
}
