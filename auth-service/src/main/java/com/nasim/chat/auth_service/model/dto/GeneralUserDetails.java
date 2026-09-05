package com.nasim.chat.auth_service.model.dto;

import java.time.Instant;
import java.util.Set;

public record GeneralUserDetails(
        String userId,
        String username,
        String avatarUrl,
        String email,
        String phoneNumber,
        Instant phoneVerifiedAt,
        Set<String> roles
) {
}
