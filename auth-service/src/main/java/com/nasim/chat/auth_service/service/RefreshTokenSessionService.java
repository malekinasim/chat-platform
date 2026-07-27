package com.nasim.chat.auth_service.service;

import java.time.Instant;

public interface RefreshTokenSessionService {

    boolean isValidToken(String tokenValue);

    void createAndRevokeRefreshToken(String userId, String tokenId, String clientId, Instant expiresAt);
}
