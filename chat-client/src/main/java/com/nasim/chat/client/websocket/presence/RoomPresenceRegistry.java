package com.nasim.chat.client.websocket.presence;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomPresenceRegistry {

    private final Map<SubscriptionKey, PresenceEntry> subscriptions =
            new ConcurrentHashMap<>();

    public void addSubscription(
            String roomCode,
            String sessionId,
            String subscriptionId,
            String userId
    ) {
        SubscriptionKey key = new SubscriptionKey(sessionId, subscriptionId);
        PresenceEntry entry = new PresenceEntry(roomCode, userId);
        subscriptions.put(key, entry);

    }

    public Optional<String> removeSubscription(
            String sessionId,
            String subscriptionId
    ) {
        SubscriptionKey key = new SubscriptionKey(sessionId, subscriptionId);
        PresenceEntry removedEntry = subscriptions.remove(key);
        if (removedEntry == null) return Optional.empty();
        return Optional.of(removedEntry.roomCode);
    }

    public Set<String> removeSession(String sessionId) {
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

    public List<String> getPresentUsers(String roomCode) {
        return subscriptions.values().stream().filter(
                presenceEntry -> presenceEntry.roomCode.equals(roomCode)
        ).map(PresenceEntry::userId).distinct().toList();
    }

    public int getPresenceCount(String roomCode) {
       return this.getPresentUsers(roomCode).size();
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