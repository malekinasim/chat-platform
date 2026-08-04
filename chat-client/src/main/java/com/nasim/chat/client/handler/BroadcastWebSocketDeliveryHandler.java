package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BroadcastWebSocketDeliveryHandler implements WebSocketDeliveryHandler {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public BroadcastWebSocketDeliveryHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.BROADCAST;
    }

    @Override
    public void deliver(PublishedChatMessage message) {
        System.out.println("Publishing to /topic/public: " + message);
        MessageHeaders headers = new MessageHeaders(Map.of("chatMessageId", message.messageId()));

        simpMessagingTemplate.convertAndSend(
                "/topic/public",
                message,
                headers
        );

        System.out.println("Publication completed");


    }
}
