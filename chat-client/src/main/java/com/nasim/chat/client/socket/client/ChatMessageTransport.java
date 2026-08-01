package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.dto.OutgoingChatRequest;

public interface ChatMessageTransport {
    void publish(OutgoingChatRequest outgoingChatRequest);
}
