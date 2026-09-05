package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.dto.OnlineUser;
import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.service.RoomPresenceService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoomPresenceControllerTest {
    @Test
    void returnsServiceResponseAndExtractsAuthenticatedUserAndToken() {
        RoomPresenceService service = mock(RoomPresenceService.class);
        RoomPresenceController controller = new RoomPresenceController(service);
        Jwt jwt = Jwt.withTokenValue("access-token")
                .header("alg", "none")
                .subject("requester-7")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        RoomPresenceResponse expected = new RoomPresenceResponse(
                "room-a", List.of(new OnlineUser("1", "alice", "avatar")));
        when(service.getRoomPresence("room-a", "requester-7", "access-token")).thenReturn(expected);

        RoomPresenceResponse response = controller.getPresence("room-a", authentication);

        assertThat(response).isSameAs(expected);
        verify(service).getRoomPresence("room-a", "requester-7", "access-token");
    }
}
