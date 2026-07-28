package com.nasim.chat.controller;

import com.nasim.chat.client.security.securityUtils;
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
        tcpChatGateway.send(
                OutgoingChatRequest.broadcastText(
                        securityUtils.authenticatedUsername(principal),
                        messageDto.text()
                )
        );
    }

    @MessageMapping("/chat/room/{roomCode}")
    public void sendToRoom(
            ChatMessageDto messageDto,
            @DestinationVariable String roomCode,
            Principal principal
    ) {
        tcpChatGateway.send(
                OutgoingChatRequest.groupText(
                        securityUtils.authenticatedUsername(principal),
                        roomCode,
                        messageDto.text()
                )
        );
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
