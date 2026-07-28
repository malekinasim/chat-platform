package com.nasim.chat.client.controller;

import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.socket.client.ChatMessageTransport;
import com.nasim.chat.client.model.dto.ChatMessageDto;
import com.nasim.chat.client.model.dto.OutgoingChatRequest;
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
    private final ChatMessageTransport chatMessageTransport;

    public ChatBrowserController(ChatMessageTransport chatMessageTransport) {
        this.chatMessageTransport = chatMessageTransport;
    }
    @MessageMapping("/chat/public")
    public void sendPublic(ChatMessageDto messageDto, Principal principal) {
        chatMessageTransport.publish(
                OutgoingChatRequest.broadcastText(
                        SecurityUtils.authenticatedUsername(principal),
                        messageDto.text()
                )
        );
    }

    @MessageMapping("/chat/room/{roomCode}")
    public void sendToRoom(
            ChatMessageDto messageDto,
            @DestinationVariable(value = "roomCode") String roomCode,
            Principal principal
    ) {
        chatMessageTransport.publish(
                OutgoingChatRequest.groupText(
                        SecurityUtils.authenticatedUsername(principal),
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
