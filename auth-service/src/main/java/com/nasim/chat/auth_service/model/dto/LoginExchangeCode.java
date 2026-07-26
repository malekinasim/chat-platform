package com.nasim.chat.auth_service.model.dto;

import java.time.Instant;
import java.util.List;

public record LoginExchangeCode(
        String userId,
        List<String> roles,
        List<String> allowedAudiences,
        Instant expiresAt
) {}