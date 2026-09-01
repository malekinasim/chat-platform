package com.nasim.chat.client.storages.impl;


import com.nasim.chat.client.storages.RoomPresenceStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@Profile("redis-presence")
public class RedisRoomPresenceStore
        implements RoomPresenceStore {
    @Override
    public void subscribe(String sessionId, String subscriptionId, String userId, String roomCode) {
        
    }

    @Override
    public Optional<String> unsubscribe(String sessionId, String subscriptionId) {
        return Optional.empty();
    }

    @Override
    public Set<String> disconnect(String sessionId) {
        return Set.of();
    }

    @Override
    public Set<String> onlineUsers(String roomCode) {
        return Set.of();
    }

    @Override
    public int onlineUserCount(String roomCode) {
        return 0;
    }
    // Redis implementation
}