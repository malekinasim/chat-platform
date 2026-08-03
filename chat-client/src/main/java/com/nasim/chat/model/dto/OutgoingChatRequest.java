package com.nasim.chat.model.dto;

public record OutgoingChatRequest(
        DeliveryType deliveryType,
        MessageContentType messageContentType,
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
                MessageContentType.TEXT,
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
                MessageContentType.TEXT,
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
                MessageContentType.TEXT,
                sender,
                null,
                text,
                room
        );
    }
}