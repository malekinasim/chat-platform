package com.nasim.chat.client.websocket;

import com.nasim.chat.client.socket.client.TcpChatGateway;
import com.nasim.chat.model.ChatMessageDto;
import com.nasim.chat.model.OutgoingChatRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Controller
@RestController
public class ChatBrowserController {
    private final TcpChatGateway tcpChatGateway;

    public ChatBrowserController(TcpChatGateway tcpChatGateway) {
        this.tcpChatGateway = tcpChatGateway;
    }
    @MessageMapping("/chat/public")
    public void sendPublic(ChatMessageDto messageDto, Principal principal) {
          //TODO return fix it after config JWT and OIDC
        tcpChatGateway.send(OutgoingChatRequest.broadcastText(
                principal!=null ? principal.getName():"test-user", messageDto.text()));
    }

    @MessageMapping("/chat/room/{room-code}")
    public void sendToRoom(ChatMessageDto messageDto, @DestinationVariable("room-code") String roomCode, Principal principal) {
        //TODO return fix it after config JWT and OIDC
        tcpChatGateway.send(OutgoingChatRequest.groupText(
                principal!=null ? principal.getName():"test-user",roomCode, messageDto.text()));
    }

    @GetMapping("/api/chat/list/user-rooms")
    public ResponseEntity<?> sendUserRoomList(Principal principal) {
        return new ResponseEntity<>(List.of("test1","test2"), HttpStatus.OK);
    }
    @GetMapping("/api/admin/rooms")
    public ResponseEntity<?> sendAllRoomList(Principal principal) {
        return new ResponseEntity<>(List.of("test1","test2","test3"), HttpStatus.OK);
    }
    @GetMapping("/api/test/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> test(Principal principal) {
        return ResponseEntity.ok(List.of("test1", "test2", "test3"));
    }
}
