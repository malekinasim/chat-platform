package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.storages.InstanceSessionStore;
import com.nasim.chat.client.storages.RoomPresenceStore;
import com.nasim.chat.client.storages.impl.InMemoryRoomPresenceStore;
import com.nasim.chat.client.storages.impl.PresenceInstanceIdentity;
import com.nasim.chat.client.websocket.WebSocketConfig;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.Set;

@Component
public class RoomPresenceEventListener {



    private final RoomPresenceStore presenceRegistry;
    private final InstanceSessionStore instanceSessionStore;
    private final PresenceInstanceIdentity identity;
    public RoomPresenceEventListener(RoomPresenceStore presenceRegistry, InstanceSessionStore instanceSessionStore, PresenceInstanceIdentity identity) {
        this.presenceRegistry = presenceRegistry;
        this.instanceSessionStore = instanceSessionStore;
        this.identity = identity;
    }

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        if (sessionId == null) {
            return;
        }

        instanceSessionStore.addSession(
                identity.getInstanceId(),
                sessionId
        );
    }
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        if (destination == null ||
                !destination.startsWith(WebSocketConfig.ROOM_TOPIC_PREFIX)) {
            return;
        }

        if (sessionId == null || subscriptionId == null) {
            return;
        }

        String roomCode =
                destination.substring(WebSocketConfig.ROOM_TOPIC_PREFIX.length());

        if (roomCode.isBlank()) {
            return;
        }

        Principal principal = accessor.getUser();

        String userId = principal != null
                ? principal.getName()
                : sessionId;
        presenceRegistry.subscribe( sessionId, subscriptionId, userId,roomCode);

        System.out.printf("Present in room %s: %s%n", roomCode, presenceRegistry.onlineUsers(roomCode));

    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String subscriptionId = accessor.getSubscriptionId();

        if (sessionId == null || subscriptionId == null) {
            return;
        }
        presenceRegistry.unsubscribe(sessionId, subscriptionId)
                .ifPresent(roomCode ->
                        System.out.printf("Present in room %s: %s%n", roomCode, presenceRegistry.onlineUsers(roomCode)));

    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = event.getSessionId();
        Set<String> affectedRooms = presenceRegistry.disconnect(sessionId);
        for (String roomCode : affectedRooms) {
            System.out.printf("Present in room %s: %s%n", roomCode, presenceRegistry.onlineUsers(roomCode));
        }
        instanceSessionStore.removeSession(
                identity.getInstanceId(),
                sessionId
        );
    }
}