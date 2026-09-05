package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.service.RoomPresenceService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            JwtAuthenticationToken authentication
    ) {
        return roomPresenceService.getRoomPresence(
                roomCode,
                SecurityUtils.authenticatedUsername(authentication),
                authentication.getToken().getTokenValue()
        );
    }
}
