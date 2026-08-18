package com.nasim.chat.client.websocket.channelInterceptor;

import com.nasim.chat.client.websocket.WebSocketConfig;
import com.nasim.chat.client.websocket.eventListener.PrivateSubscriptionReadyEvent;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import java.security.Principal;

@Component
public class PrivateSubscriptionReadyInterceptor implements ExecutorChannelInterceptor {

    private final ApplicationEventPublisher eventPublisher;

    public PrivateSubscriptionReadyInterceptor(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, @Nullable Exception ex) {

        if (ex != null ||
                !(handler instanceof SimpleBrokerMessageHandler)) {
            return;
        }
        StompHeaderAccessor  accessor=StompHeaderAccessor.wrap(message);
        String originalDestination =
                accessor.getFirstNativeHeader(
                        SimpMessageHeaderAccessor.ORIGINAL_DESTINATION
                );
        if (accessor.getMessageType() != SimpMessageType.SUBSCRIBE ||
                !WebSocketConfig.PRIVATE_TOPIC_PREFIX.equals(
                        originalDestination
                )) {
            return;
        }
        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();

        if (sessionId == null || principal == null ||
                principal.getName() == null ||
                principal.getName().isBlank()) {
            return;
        }
        eventPublisher.publishEvent(new PrivateSubscriptionReadyEvent(sessionId, principal.getName()));
    }
}
