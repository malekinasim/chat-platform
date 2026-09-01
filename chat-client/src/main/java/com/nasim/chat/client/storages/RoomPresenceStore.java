package com.nasim.chat.client.storages;

import java.util.Optional;
import java.util.Set;

public interface RoomPresenceStore {

    void subscribe(
            String sessionId,
            String subscriptionId,
            String userId,
            String roomCode
    );

    Optional<String> unsubscribe(
            String sessionId,
            String subscriptionId
    );

    Set<String> disconnect(String sessionId);

    Set<String> onlineUsers(String roomCode);

    int onlineUserCount(String roomCode);
}