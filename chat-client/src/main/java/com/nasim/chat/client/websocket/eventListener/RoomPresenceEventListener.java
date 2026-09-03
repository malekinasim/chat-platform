package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.storages.RoomPresenceStore;
import com.nasim.chat.client.websocket.WebSocketConfig;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

@Component
public class RoomPresenceEventListener {

    private final RoomPresenceStore roomPresenceStore;

    public RoomPresenceEventListener(
            RoomPresenceStore roomPresenceStore
    ) {
        this.roomPresenceStore = roomPresenceStore;
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();
        Principal principal = accessor.getUser();

        if (destination == null ||
                !destination.startsWith(
                        WebSocketConfig.ROOM_TOPIC_PREFIX
                )) {
            return;
        }

        if (sessionId == null ||
                subscriptionId == null ||
                principal == null ||
                principal.getName() == null ||
                principal.getName().isBlank()) {
            return;
        }

        String roomCode = destination.substring(
                WebSocketConfig.ROOM_TOPIC_PREFIX.length()
        );

        if (roomCode.isBlank()) {
            return;
        }

        roomPresenceStore.subscribe(
                sessionId,
                subscriptionId,
                principal.getName(),
                roomCode
        );
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        if (sessionId == null || subscriptionId == null) {
            return;
        }

        roomPresenceStore.unsubscribe(
                sessionId,
                subscriptionId
        );
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        roomPresenceStore.disconnect(
                event.getSessionId()
        );
    }
}