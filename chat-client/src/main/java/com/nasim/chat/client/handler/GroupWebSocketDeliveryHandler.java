package com.nasim.chat.client.handler;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.DeliveryType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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
    public void deliver(ChatMessage message) {
        System.out.println("Publishing to /topic/room/%s ".formatted(message.room()) + message);

        simpMessagingTemplate.convertAndSend(
                "/topic/room/%s".formatted(message.room()),
                message
        );

        System.out.println("Publication completed");


    }
}
