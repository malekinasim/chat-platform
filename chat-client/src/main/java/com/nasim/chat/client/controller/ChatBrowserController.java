package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.mapper.MessageMapper;
import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.MessageService;
import com.nasim.chat.client.service.impl.ReceiverResolveRegistry;
import com.nasim.chat.client.socket.client.ChatMessageTransport;
import com.nasim.chat.model.dto.ChatGroupDto;
import com.nasim.chat.model.dto.ChatMessageDto;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class ChatBrowserController {
    private final ChatMessageTransport chatMessageTransport;
    private final SimpUserRegistry simpUserRegistry;
    private final GroupMembershipService groupMembershipService;
    private final ReceiverResolveRegistry receiverResolverRegistry;
    private final MessageService messageService;
    public ChatBrowserController(ChatMessageTransport chatMessageTransport, SimpUserRegistry simpUserRegistry, GroupMembershipService groupMembershipService, ReceiverResolveRegistry receiverResolverRegistry, MessageService messageService) {
        this.chatMessageTransport = chatMessageTransport;
        this.simpUserRegistry = simpUserRegistry;
        this.groupMembershipService = groupMembershipService;
        this.receiverResolverRegistry = receiverResolverRegistry;
        this.messageService = messageService;
    }
    @MessageMapping("/chat/public")
    public void sendPublic(ChatMessageDto messageDto, Principal principal, JwtAuthenticationToken authentication) {
        SendMessageCommand message=    SendMessageCommand.broadcastText(
                SecurityUtils.authenticatedUsername(principal),
                messageDto.text()
        );
        List<String> receiverIds =
                receiverResolverRegistry.get(message.deliveryType()).resolveReceiverIds(message,authentication);
        if(!receiverIds.isEmpty())
            this.sendMessage(message,receiverIds);
    }
    @MessageMapping("/chat/private/{receiverId}")
    public void sendPrivate(ChatMessageDto messageDto, @DestinationVariable String receiverId, Principal principal, JwtAuthenticationToken authentication) {
        SendMessageCommand message=  SendMessageCommand.privateText(principal.getName(), receiverId, messageDto.text());
        List<String> receiverIds = receiverResolverRegistry.get(message.deliveryType()).resolveReceiverIds(message,authentication);
        if (!receiverIds.isEmpty())
            this.sendMessage(message,receiverIds);
    }

    @MessageMapping("/chat/room/{roomCode}")
    public void sendToRoom(ChatMessageDto messageDto, @DestinationVariable String roomCode, Principal principal, JwtAuthenticationToken authentication) {
        String userId = SecurityUtils.authenticatedUsername(principal);
        if(!groupMembershipService.hasActiveMembership(userId, roomCode))
            throw new AuthorizationDeniedException("you don't have valid access right for sending message in this group");

        SendMessageCommand message= SendMessageCommand.groupText(SecurityUtils.authenticatedUsername(principal),
                            roomCode, messageDto.text());
        List<String> receiverIds = receiverResolverRegistry.get(message.deliveryType()).resolveReceiverIds(message,authentication);
        if(!receiverIds.isEmpty())
            this.sendMessage(message,receiverIds);
    }
    private void sendMessage(SendMessageCommand command, List<String> receiverIds) {
        Message savedMessage = messageService.saveTextMessage(command, receiverIds);
        chatMessageTransport.publish(MessageMapper.toPublishedMessage(savedMessage, command));
    }

    @GetMapping("/api/chat/list/user-groups")
    public ResponseEntity<?> sendUserRoomList(Principal principal) {
        String userId = SecurityUtils.authenticatedUsername(principal);

        List <ChatGroupDto> groups=groupMembershipService.getUserActiveGroup(userId);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/api/chat/list/online-users")
    public List<String> getOnlineUsers(
            Authentication authentication
    ) {
        String currentUserId = authentication.getName();
        return simpUserRegistry.getUsers()
                .stream()
                .map(SimpUser::getName)
                .filter(userId -> !userId.equals(currentUserId))
                .sorted()
                .toList();
    }

}
