package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.service.RoomPresenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat/rooms")
public class RoomPresenceController {
    private final RoomPresenceService roomPresenceService;

    public RoomPresenceController(RoomPresenceService roomPresenceService) {
        this.roomPresenceService = roomPresenceService;
    }

    @GetMapping("/{roomCode}/presence")
    public RoomPresenceResponse getPresence(
            @PathVariable String roomCode,
            Principal principal
    ) {
        return roomPresenceService.getRoomPresence(
                roomCode,
                SecurityUtils.authenticatedUsername(principal)
        );
    }
}
