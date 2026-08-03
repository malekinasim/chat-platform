package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TcpChatGateway implements ChatMessageTransport {
    private final ChatConnection chatConnection;

    public TcpChatGateway(ChatConnection chatConnection) {
        this.chatConnection = chatConnection;
    }

    public void publish(SendMessageCommand sendMessageCommand) {
        if (!chatConnection.isOpen()) {
                throw new IllegalStateException(
                        "Cannot send because the chat-server connection is closed"
                );
        }
        ChatMessage message = new ChatMessage(
                sendMessageCommand.deliveryType(),
                sendMessageCommand.messageContentType(),
                sendMessageCommand.sender(),
                sendMessageCommand.receiver(),
                sendMessageCommand.text(),
                sendMessageCommand.room()
        );
        try {
            chatConnection.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
