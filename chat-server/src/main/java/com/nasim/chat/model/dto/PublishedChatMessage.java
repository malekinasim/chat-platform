package com.nasim.chat.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public record PublishedChatMessage(
        DeliveryType deliveryType,
        MessageContentType messageContentType,
        String sender,
        String receiver,
        String content,
        String room,
        Long messageId,
        LocalDateTime createdAt
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public PublishedChatMessage {
        Objects.requireNonNull(
                deliveryType,
                "deliveryType must not be null"
        );

        Objects.requireNonNull(
                messageContentType,
                "contentType must not be null"
        );

        Objects.requireNonNull(
                sender,
                "sender must not be null"
        );

        Objects.requireNonNull(
                content,
                "content must not be null"
        );

        if (deliveryType == DeliveryType.PRIVATE) {
            Objects.requireNonNull(
                    receiver,
                    "receiver is required for private messages"
            );
        }

        if (deliveryType == DeliveryType.GROUP) {
            Objects.requireNonNull(
                    room,
                    "room is required for group messages"
            );
        }
    }
}