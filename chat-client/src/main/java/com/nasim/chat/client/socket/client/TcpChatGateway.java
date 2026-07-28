package com.nasim.chat.client.socket.client;

import com.nasim.chat.client.model.dto.ChatMessage;
import com.nasim.chat.client.model.dto.OutgoingChatRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TcpChatGateway implements ChatMessageTransport {
    private final ChatConnection chatConnection;

    public TcpChatGateway(ChatConnection chatConnection) {
        this.chatConnection = chatConnection;
    }

    public void publish(OutgoingChatRequest outgoingChatRequest) {
        if (!chatConnection.isOpen()) {
                throw new IllegalStateException(
                        "Cannot send because the chat-server connection is closed"
                );
        }
        ChatMessage message = new ChatMessage(
                outgoingChatRequest.deliveryType(),
                outgoingChatRequest.contentType(),
                outgoingChatRequest.sender(),
                outgoingChatRequest.receiver(),
                outgoingChatRequest.text(),
                outgoingChatRequest.room()
        );
        try {
            chatConnection.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
