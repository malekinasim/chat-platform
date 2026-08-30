package com.nasim.chat.client.model.dto;



import java.time.LocalDateTime;

public record HistoryCursor(
        LocalDateTime beforeCreatedAt,
        Long beforeMessageId
) {
}