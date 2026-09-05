package com.nasim.chat.client.storages.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRoomPresenceStoreTest {
    @Test
    void reportsOneUserForMultipleSubscriptions() {
        InMemoryRoomPresenceStore store = new InMemoryRoomPresenceStore();
        store.subscribe("session-1", "subscription-1", "user-1", "room");
        store.subscribe("session-2", "subscription-2", "user-1", "room");

        assertThat(store.onlineUsers("room")).containsExactly("user-1");
        assertThat(store.onlineUserCount("room")).isOne();
    }
}
