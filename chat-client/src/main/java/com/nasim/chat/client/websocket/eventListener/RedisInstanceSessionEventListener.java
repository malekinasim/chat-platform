package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.storages.InstanceSessionStore;
import com.nasim.chat.client.storages.RoomPresenceStore;
import com.nasim.chat.client.storages.impl.PresenceInstanceIdentity;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Profile("redis-presence")
public class RedisInstanceSessionEventListener {

    private final InstanceSessionStore instanceSessionStore;
    private final RoomPresenceStore roomPresenceStore;
    private final PresenceInstanceIdentity identity;

    public RedisInstanceSessionEventListener(InstanceSessionStore instanceSessionStore, RoomPresenceStore roomPresenceStore, PresenceInstanceIdentity identity) {
        this.instanceSessionStore = instanceSessionStore;
        this.roomPresenceStore = roomPresenceStore;
        this.identity = identity;
    }
    @EventListener
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void handleDisconnect(
            SessionDisconnectEvent event
    ) {
        roomPresenceStore.disconnect(
                event.getSessionId()
        );
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
}