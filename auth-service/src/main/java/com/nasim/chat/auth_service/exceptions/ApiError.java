package com.nasim.chat.auth_service.exceptions;

public record ApiError(
        String code,
        String message
) {
}