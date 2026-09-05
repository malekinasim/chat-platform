package com.nasim.chat.client.model.dto;

import java.time.Instant;
import java.util.Set;

/**
 * Chat client's representation of the auth-service GeneralUserDetails contract.
 */
public record DirectoryUser(
        String userId,
        String username,
        String avatarUrl,
        String email,
        String phoneNumber,
        Instant phoneVerifiedAt,
        Set<String> roles
) {
}
