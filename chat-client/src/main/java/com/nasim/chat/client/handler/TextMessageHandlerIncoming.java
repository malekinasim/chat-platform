package com.nasim.chat.client.handler;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.ContentType;
import org.springframework.stereotype.Component;

@Component
public class TextMessageHandlerIncoming implements IncomingContentHandler {
    @Override
    public ContentType supportedType() {
        return ContentType.TEXT;
    }

    @Override
    public ChatMessage handle(ChatMessage message) {
        String cleanedContent = message.content().trim();

        if (cleanedContent.isEmpty()) {
            throw new IllegalArgumentException(
                    "Text message cannot be empty"
            );
        }

        return new ChatMessage(
                message.deliveryType(),
                message.contentType(),
                message.sender(),
                message.receiver(),
                cleanedContent,
                message.room()
        );
    }
}
