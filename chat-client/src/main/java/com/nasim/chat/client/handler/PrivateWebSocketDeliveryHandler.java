package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public void deliver(PublishedChatMessage message) {
        System.out.println("Publishing private message to user " + message.receiver());
        MessageHeaders headers = new MessageHeaders(Map.of("chatMessageId", message.messageId()));
        simpMessagingTemplate.convertAndSendToUser(
                message.receiver(),
                "/queue/private",
                message,
                headers
        );
        System.out.println("Publication completed");

    }
}