package com.nasim.chat.client.storages.impl;

import com.nasim.chat.client.storages.RoomPresenceStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Profile("!redis-presence")
public class InMemoryRoomPresenceStore implements RoomPresenceStore {

    private final Map<SubscriptionKey, PresenceEntry> subscriptions =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(
            String sessionId,
            String subscriptionId,
            String userId,
            String roomCode
    ) {
        SubscriptionKey key = new SubscriptionKey(sessionId, subscriptionId);
        PresenceEntry entry = new PresenceEntry(roomCode, userId);
        subscriptions.put(key, entry);

    }
    @Override
    public Optional<String> unsubscribe(
            String sessionId,
            String subscriptionId
    ){
        SubscriptionKey key = new SubscriptionKey(sessionId, subscriptionId);
        PresenceEntry removedEntry = subscriptions.remove(key);
        if (removedEntry == null) return Optional.empty();
        return Optional.of(removedEntry.roomCode);
    }
    @Override
    public Set<String> disconnect(String sessionId) {
        Set<String> affectedRooms = new HashSet<>();
        subscriptions.forEach((key, entry) -> {
            if (key.sessionId.equals(sessionId)) {
                boolean removed = subscriptions.remove(key, entry);
                if (removed)
                    affectedRooms.add(entry.roomCode());
            }
        });
        return affectedRooms;
    }
    @Override
    public Set<String> onlineUsers (String roomCode) {
        return subscriptions.values().stream().filter(
                presenceEntry -> presenceEntry.roomCode.equals(roomCode)
        ).map(PresenceEntry::userId).distinct().collect(Collectors.toSet());
    }
    @Override
    public int onlineUserCount(String roomCode) {

        return this.onlineUsers(roomCode).size();
    }

    private record SubscriptionKey(
            String sessionId,
            String subscriptionId
    ) {
    }

    private record PresenceEntry(
            String roomCode,
            String userId
    ) {
    }
}