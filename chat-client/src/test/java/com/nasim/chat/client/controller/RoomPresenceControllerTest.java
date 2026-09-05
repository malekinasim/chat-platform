package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.dto.OnlineUser;
import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.service.RoomPresenceService;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomPresenceControllerTest {
    @Test
    void returnsServiceResponseAndExtractsAuthenticatedUser() {
        RoomPresenceService service = mock(RoomPresenceService.class);
        RoomPresenceController controller = new RoomPresenceController(service);
        Principal principal = () -> "requester-7";
        RoomPresenceResponse expected = new RoomPresenceResponse(
                "room-a", List.of(new OnlineUser("1", "alice", "avatar")));
        when(service.getRoomPresence("room-a", "requester-7")).thenReturn(expected);

        RoomPresenceResponse response = controller.getPresence("room-a", principal);

        assertThat(response).isSameAs(expected);
        verify(service).getRoomPresence("room-a", "requester-7");
    }
}
