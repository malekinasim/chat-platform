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
        System.out.println("Publishing private message to user " + message.receiver());

        simpMessagingTemplate.convertAndSendToUser(
                message.receiver(),
                "/queue/private",
                message
        );

        System.out.println("Publication completed");


    }
}