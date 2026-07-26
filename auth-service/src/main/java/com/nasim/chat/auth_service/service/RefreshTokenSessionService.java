package com.nasim.chat.auth_service.service;

import java.time.Instant;

public interface RefreshTokenSessionService {

    void createAndRevokeRefreshToken(String userId, String tokenId, Instant expiresAt);
}
