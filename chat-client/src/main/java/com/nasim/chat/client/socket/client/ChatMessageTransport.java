package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.dto.SendMessageCommand;

public interface ChatMessageTransport {
    void publish(SendMessageCommand sendMessageCommand);
}
