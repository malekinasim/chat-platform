package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GroupWebSocketDeliveryHandler implements WebSocketDeliveryHandler {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GroupWebSocketDeliveryHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.GROUP;
    }

    @Override
    public void deliver(PublishedChatMessage message) {
        System.out.println("Publishing to /topic/room/%s ".formatted(message.room()) + message);
        MessageHeaders headers = new MessageHeaders(Map.of("chatMessageId", message.messageId()));

        simpMessagingTemplate.convertAndSend(
                "/topic/room/%s".formatted(message.room()),
                message,
                headers
        );

        System.out.println("Publication completed");


    }
}
