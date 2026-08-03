package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.MessageContentType;
import org.springframework.stereotype.Component;

@Component
public class TextMessageHandlerIncoming implements IncomingContentHandler {
    @Override
    public MessageContentType supportedType() {
        return MessageContentType.TEXT;
    }

    @Override
    public PublishedChatMessage handle(PublishedChatMessage message) {
        String cleanedContent = message.content().trim();

        if (cleanedContent.isEmpty()) {
            throw new IllegalArgumentException(
                    "Text message cannot be empty"
            );
        }

        return new PublishedChatMessage(
                message.deliveryType(),
                message.messageContentType(),
                message.sender(),
                message.receiver(),
                cleanedContent,
                message.room()
        );
    }
}
