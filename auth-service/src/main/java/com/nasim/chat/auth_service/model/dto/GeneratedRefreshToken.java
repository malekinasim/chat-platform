package com.nasim.chat.auth_service.model.dto;

import java.time.Instant;

public record GeneratedRefreshToken(
        String rawRefreshToken,
        String hashRefreshToken,
        Instant expiresAt
) {
}