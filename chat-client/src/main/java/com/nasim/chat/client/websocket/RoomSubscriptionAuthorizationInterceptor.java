package com.nasim.chat.client.websocket;

import com.nasim.chat.client.service.GroupMembershipService;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class RoomSubscriptionAuthorizationInterceptor implements ChannelInterceptor {
    private final GroupMembershipService groupMembershipService;

    public RoomSubscriptionAuthorizationInterceptor(GroupMembershipService groupMembershipService) {
        this.groupMembershipService = groupMembershipService;
    }

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        if (destination != null && destination.startsWith(WebSocketConfig.ROOM_TOPIC_PREFIX)) {
            String roomCode =
                    destination.substring(WebSocketConfig.ROOM_TOPIC_PREFIX.length());
            Principal principal = accessor.getUser();
            if (roomCode.isBlank() || principal==null || principal.getName().equals("anonymous_user"))
                return null;
            if(!groupMembershipService.hasActiveMembership(principal.getName(),roomCode)) return  null;
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
