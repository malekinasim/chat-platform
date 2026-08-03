package com.nasim.chat.client.model.entity.mapper;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;

public class MassageMapper {
    public static PublishedChatMessage toPublishedMessage(
            Message savedMessage,
            SendMessageCommand command
    ) {
        return new PublishedChatMessage(savedMessage.getDeliveryType(),savedMessage.getMessageContentType(),
                savedMessage.getSenderId(),command.receiver(),savedMessage.getTextContent(),command.room(),
                savedMessage.getId(),savedMessage.getCreatedAt());
    }
}
