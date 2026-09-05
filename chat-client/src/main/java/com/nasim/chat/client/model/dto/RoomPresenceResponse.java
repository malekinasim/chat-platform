package com.nasim.chat.client.model.dto;

import java.util.List;

public record RoomPresenceResponse(
        String roomCode,
        List<OnlineUser> onlineUsers,
        int onlineUserCount
) {
    public RoomPresenceResponse(String roomCode, List<OnlineUser> onlineUsers) {
        this(roomCode, List.copyOf(onlineUsers), onlineUsers.size());
    }
}
