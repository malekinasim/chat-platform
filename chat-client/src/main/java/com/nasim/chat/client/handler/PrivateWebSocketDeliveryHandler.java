package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrivateWebSocketDeliveryHandler implements WebSocketDeliveryHandler {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PrivateWebSocketDeliveryHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.PRIVATE;
    }

    @Override
    public void deliver(ChatMessage message) {
        System.out.println("Publishing to /queue/user/%s ".formatted(message.receiver()) + message);

        simpMessagingTemplate.convertAndSend(
                "/queue/user/%s".formatted(message.receiver()),
                message
        );

        System.out.println("Publication completed");


    }
}
