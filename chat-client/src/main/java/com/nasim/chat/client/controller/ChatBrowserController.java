package com.nasim.chat.client.controller;

import com.nasim.chat.client.model.dto.MessageHistoryResponse;
import com.nasim.chat.client.model.dto.UnreadMessageCount;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.mapper.MessageMapper;
import com.nasim.chat.client.security.SecurityUtils;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.service.MessageService;
import com.nasim.chat.client.service.impl.ReceiverResolveRegistry;
import com.nasim.chat.client.socket.client.ChatMessageTransport;
import com.nasim.chat.model.dto.ChatGroupDto;
import com.nasim.chat.model.dto.ChatMessageDto;
import com.nasim.chat.model.dto.MessageDeliveredCommand;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ChatBrowserController {
    private final ChatMessageTransport chatMessageTransport;
    private final SimpUserRegistry simpUserRegistry;
    private final GroupMembershipService groupMembershipService;
    private final ReceiverResolveRegistry receiverResolverRegistry;
    private final MessageService messageService;
    private final MessageReceiverService messageReceiverService;
    public ChatBrowserController(ChatMessageTransport chatMessageTransport, SimpUserRegistry simpUserRegistry, GroupMembershipService groupMembershipService, ReceiverResolveRegistry receiverResolverRegistry, MessageService messageService, MessageReceiverService messageReceiverService) {
        this.chatMessageTransport = chatMessageTransport;
        this.simpUserRegistry = simpUserRegistry;
        this.groupMembershipService = groupMembershipService;
        this.receiverResolverRegistry = receiverResolverRegistry;
        this.messageService = messageService;
        this.messageReceiverService = messageReceiverService;
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

    @MessageMapping("/chat/messages/delivered")
    public void markMessageAsDelivered(MessageDeliveredCommand command, Principal principal) {
        String receiverId = SecurityUtils.authenticatedUsername(principal);
        messageReceiverService.markAsDelivered(command.messageId(), receiverId);
    }

    @MessageMapping("/chat/messages/read")
    public void markMessageAsRead(MessageDeliveredCommand command, Principal principal) {
        String receiverId = SecurityUtils.authenticatedUsername(principal);
        messageReceiverService.markAsRead(command.messageId(), receiverId);
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

    @GetMapping("/api/chat/private/history")
    public MessageHistoryResponse getPrivateHistory(
            Principal principal,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime beforeCreatedAt,
            @RequestParam(required = false)
            Long beforeMessageId,
            @RequestParam(defaultValue = "50")
            int limit
    ) {
        String userId =
                SecurityUtils.authenticatedUsername(principal);

        if (limit < 1 || limit > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "limit must be between 1 and 100"
            );
        }

        return messageService.getPrivateHistory(
                userId,
                beforeCreatedAt,
                beforeMessageId,
                limit
        );
    }

    @GetMapping("/api/chat/group/history/{roomCode}")
    public MessageHistoryResponse getGroupHistory(
            Principal principal,
            @PathVariable(value = "roomCode") String roomCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime beforeCreatedAt,
            @RequestParam(required = false)
            Long beforeMessageId,
            @RequestParam(defaultValue = "50")
            int limit
    ) {

        String userId =
                SecurityUtils.authenticatedUsername(principal);
        if (limit < 1 || limit > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "limit must be between 1 and 100"
            );
        }

        return messageService.getGroupHistory(
                roomCode,
                userId,
                beforeCreatedAt,
                beforeMessageId,
                limit
        );
    }


    @GetMapping("/api/chat/broadcast/history/{roomCode}")
    public MessageHistoryResponse getBroadcastHistory(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime beforeCreatedAt,
            @RequestParam(required = false)
            Long beforeMessageId,
            @RequestParam(defaultValue = "50")
            int limit
    ) {


        if (limit < 1 || limit > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "limit must be between 1 and 100"
            );
        }

        return messageService.getBroadcastHistory(
                beforeCreatedAt,
                beforeMessageId,
                limit
        );
    }

    @GetMapping("/api/chat/private/unread-counts")
    public List<UnreadMessageCount> getPrivateUnreadCounts(
            Principal principal
    ) {
        String receiverId =
                SecurityUtils.authenticatedUsername(principal);

        return messageReceiverService
                .getPrivateUnreadCounts(receiverId);
    }

    @GetMapping("/api/chat/group/unread-counts")
    public List<UnreadMessageCount> getGroupUnreadCounts( Principal principal) {
        String userId =
                SecurityUtils.authenticatedUsername(principal);
        return messageReceiverService.getGroupUnreadCounts(userId);
    }


    @GetMapping("/api/chat/broadcast/unread-counts")
    public List<UnreadMessageCount> getBroadcastUnreadCounts( Principal principal) {
        String userId =
                SecurityUtils.authenticatedUsername(principal);
        return messageReceiverService.getBroadcastUnreadCounts(userId);
    }
}
