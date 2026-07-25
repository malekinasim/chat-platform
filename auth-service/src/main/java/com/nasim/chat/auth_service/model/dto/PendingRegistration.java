package com.nasim.chat.auth_service.model.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record PendingRegistration(String issuer, String externalSubject,
                                  String email, String name, String provider,
                                  Instant expiresAt) {
}
