package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.OutgoingChatRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TcpChatGateway {
    private final ChatConnection chatConnection;

    public TcpChatGateway(ChatConnection chatConnection) {
        this.chatConnection = chatConnection;
    }

    public void send(OutgoingChatRequest outgoingChatRequest) {
        if(!chatConnection.isOpen())
            System.out.println("Server closed the connection.");
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
