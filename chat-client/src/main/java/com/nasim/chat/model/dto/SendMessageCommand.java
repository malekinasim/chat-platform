package com.nasim.chat.model.dto;

public record SendMessageCommand(
        DeliveryType deliveryType,
        MessageContentType messageContentType,
        String sender,
        String receiver,
        String text,
        String room
) {

    public static SendMessageCommand broadcastText(
            String sender,
            String text
    ) {
        return new SendMessageCommand(
                DeliveryType.BROADCAST,
                MessageContentType.TEXT,
                sender,
                null,
                text,
                null
        );
    }

    public static SendMessageCommand privateText(
            String sender,
            String receiver,
            String text
    ) {
        return new SendMessageCommand(
                DeliveryType.PRIVATE,
                MessageContentType.TEXT,
                sender,
                receiver,
                text,
                null
        );
    }

    public static SendMessageCommand groupText(
            String sender,
            String room,
            String text
    ) {
        return new SendMessageCommand(
                DeliveryType.GROUP,
                MessageContentType.TEXT,
                sender,
                null,
                text,
                room
        );
    }
}