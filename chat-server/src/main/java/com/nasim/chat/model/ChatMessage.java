package com.nasim.chat.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record ChatMessage(
        DeliveryType deliveryType,
        ContentType contentType,
        String sender,
        String receiver,
        String content,
        String room
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ChatMessage {
        Objects.requireNonNull(
                deliveryType,
                "deliveryType must not be null"
        );

        Objects.requireNonNull(
                contentType,
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