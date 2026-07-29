package com.nasim.chat.auth_service.model.dto;

import java.time.Instant;

public record PendingRegistration(
        String issuer,
        String subject,
        String provider,
        String email,
        String displayName,
        Instant expiresAt
) {
}
