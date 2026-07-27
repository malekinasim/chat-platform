package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenSessionService {

    boolean isValidToken(String tokenValue);

    Optional<RefreshTokenSession> findByTokenId(String tokenId);

    void createAndRevokeRefreshToken(String userId, String tokenId, String clientId, Instant expiresAt);
}
