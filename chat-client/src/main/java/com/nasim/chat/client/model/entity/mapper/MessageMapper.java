package com.nasim.chat.client.model.entity.mapper;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;

public class MessageMapper {
    private MessageMapper() {
    }

    public static PublishedChatMessage toPublishedMessage(
            Message savedMessage,
            SendMessageCommand command
    ) {
        return new PublishedChatMessage(savedMessage.getDeliveryType(),savedMessage.getMessageContentType(),
                savedMessage.getSenderId(),command.receiver(),savedMessage.getTextContent(),command.room(),
                savedMessage.getId(),savedMessage.getCreatedAt());
    }

    public static PublishedChatMessage toPublishedMessage(
            Message savedMessage,
            String receiverId
    ) {
        return new PublishedChatMessage(savedMessage.getDeliveryType(), savedMessage.getMessageContentType(),
                savedMessage.getSenderId(), receiverId, savedMessage.getTextContent(), null,
                savedMessage.getId(), savedMessage.getCreatedAt());
    }

    public static PublishedChatMessage toPublishedMessage(
            Message message
    ) {
        return new PublishedChatMessage(
                message.getDeliveryType(),
                message.getMessageContentType(),
                message.getSenderId(),
                message.getDestinationId(),
                message.getTextContent(),
                null,
                message.getId(),
                message.getCreatedAt()
        );
    }
}
