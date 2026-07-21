package com.nasim.chat.model;

public record OutgoingChatRequest(
        DeliveryType deliveryType,
        ContentType contentType,
        String sender,
        String receiver,
        String text,
        String room
) {

    public static OutgoingChatRequest broadcastText(
            String sender,
            String text
    ) {
        return new OutgoingChatRequest(
                DeliveryType.BROADCAST,
                ContentType.TEXT,
                sender,
                null,
                text,
                null
        );
    }

    public static OutgoingChatRequest privateText(
            String sender,
            String receiver,
            String text
    ) {
        return new OutgoingChatRequest(
                DeliveryType.PRIVATE,
                ContentType.TEXT,
                sender,
                receiver,
                text,
                null
        );
    }

    public static OutgoingChatRequest groupText(
            String sender,
            String room,
            String text
    ) {
        return new OutgoingChatRequest(
                DeliveryType.GROUP,
                ContentType.TEXT,
                sender,
                null,
                text,
                room
        );
    }
}