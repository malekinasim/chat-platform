package com.nasim.chat.client.model.dto;
import com.nasim.chat.model.dto.PublishedChatMessage;

import java.util.List;

public record MessageHistoryResponse(
        List<PublishedChatMessage> messages,
        HistoryCursor nextCursor,
        boolean hasMore
) {
}