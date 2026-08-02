package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.entity.ChatGroup;
import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.socket.client.ChatMessageTransport;
import com.nasim.chat.model.dto.ChatGroupDto;
import com.nasim.chat.model.dto.ChatMessageDto;
import com.nasim.chat.model.dto.OutgoingChatRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
@RestController
public class ChatBrowserController {
    private final ChatMessageTransport chatMessageTransport;
    private final GroupMembershipService groupMembershipService;
    public ChatBrowserController(ChatMessageTransport chatMessageTransport, GroupMembershipService groupMembershipService) {
        this.chatMessageTransport = chatMessageTransport;
        this.groupMembershipService = groupMembershipService;
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
    @MessageMapping("/chat/private/{receiverId}")
    public void sendPrivate(
            ChatMessageDto messageDto,
            @DestinationVariable String receiverId,
            Principal principal
    ) {
        chatMessageTransport.publish(
                OutgoingChatRequest.privateText(
                        principal.getName(),
                        receiverId,
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
        String userId = SecurityUtils.authenticatedUsername(principal);

        if(!groupMembershipService.hasActiveMembership(userId, roomCode))
            throw new AuthorizationDeniedException("you don't have valid access right for sending message in this group");
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
        String userId = SecurityUtils.authenticatedUsername(principal);

        List <ChatGroupDto> groups=groupMembershipService.getUserActiveGroup(userId);
        return new ResponseEntity<>(groups, HttpStatus.OK);
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
