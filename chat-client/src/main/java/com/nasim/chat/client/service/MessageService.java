package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.MessageHistoryResponse;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.model.dto.SendMessageCommand;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    Message saveTextMessage(SendMessageCommand sendMessageCommand, List<String> receiver);

    MessageHistoryResponse getPrivateHistory(
            String userId,
            LocalDateTime beforeCreatedAt,
            Long beforeMessageId,
            int limit
    );


    MessageHistoryResponse getGroupHistory(String roomCode,
                                           LocalDateTime beforeCreatedAt,
                                           Long beforeMessageId, int limit);

    MessageHistoryResponse getBroadcastHistory(LocalDateTime beforeCreatedAt, Long beforeMessageId, int limit);
}
