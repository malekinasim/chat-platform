package com.nasim.chat.auth_service.model.dto;

public record AuthenticationTokens(String accessToken,
                                   String refreshToken) {
}
