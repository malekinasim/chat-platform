package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.RoomPresenceResponse;

public interface RoomPresenceService {
    RoomPresenceResponse getRoomPresence(String roomCode, String requesterId);
}
