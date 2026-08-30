package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.PrivateHistoryResponse;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.model.dto.SendMessageCommand;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    Message saveTextMessage(SendMessageCommand sendMessageCommand, List<String> receiver);

    PrivateHistoryResponse getPrivateHistory(
            String userId,
            LocalDateTime beforeCreatedAt,
            Long beforeMessageId,
            int limit
    );

}
