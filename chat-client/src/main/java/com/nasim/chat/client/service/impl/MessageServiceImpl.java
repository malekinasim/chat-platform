package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.HistoryCursor;
import com.nasim.chat.client.model.dto.MessageHistoryResponse;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.mapper.MessageMapper;
import com.nasim.chat.client.repository.MessageRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.service.MessageService;
import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageReceiverService messageReceiverService;
    private final GroupMembershipService groupMembershipService;
    public MessageServiceImpl(MessageRepository messageRepository, MessageReceiverService messageReceiverService, GroupMembershipService groupMembershipService) {
        this.messageRepository = messageRepository;
        this.messageReceiverService = messageReceiverService;
        this.groupMembershipService = groupMembershipService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message saveTextMessage(SendMessageCommand sendMessageCommand, List<String> receiver) {
        Message message = new Message();
        message.setSenderId(sendMessageCommand.sender());
        message.setTextContent(sendMessageCommand.text());
        message.setMessageContentType(sendMessageCommand.messageContentType());
        message.setDeliveryType(sendMessageCommand.deliveryType());
        String destinationId = switch (sendMessageCommand.deliveryType()) {
            case PRIVATE -> sendMessageCommand.receiver();
            case GROUP -> sendMessageCommand.room();
            case BROADCAST -> null;
        };
        message.setDestinationId(destinationId);
        Message savedMessage = messageRepository.save(message);
        messageReceiverService.saveReceivers(savedMessage,receiver);
        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageHistoryResponse getPrivateHistory(
            String userId,
            LocalDateTime beforeCreatedAt,
            Long beforeMessageId,
            int limit
    ) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 100"
            );
        }


        Page<Message> result = messageRepository.findPrivateHistory(
                userId,
                beforeCreatedAt,
                beforeMessageId,
                PageRequest.of(0, limit )
        );

        boolean hasMore = !result.isLast();

        HistoryCursor nextCursor = null;

        if (hasMore && !result.isEmpty()) {
            Message oldestMessage =result.getContent().get(result.getNumberOfElements() -1);
            nextCursor = new HistoryCursor(
                    oldestMessage.getCreatedAt(),
                    oldestMessage.getId()
            );
        }
        List<Message> selectedMessages =
                new ArrayList<>(result.getContent());
        Collections.reverse(selectedMessages);

        List<PublishedChatMessage> messages =
                selectedMessages.stream()
                        .map(MessageMapper::toPublishedMessage)
                        .toList();

        return new MessageHistoryResponse(
                messages,
                nextCursor,
                hasMore
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MessageHistoryResponse getGroupHistory(String roomCode,
                                                  String userId, LocalDateTime beforeCreatedAt,
                                                  Long beforeMessageId, int limit) {

        if(!groupMembershipService.hasActiveMembership(userId,roomCode))
            throw new AccessDeniedException( "User does not have access to this group");


        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 100"
            );
        }


        Page<Message> result = messageRepository.findGroupHistory(
                roomCode,
                beforeCreatedAt,
                beforeMessageId,
                PageRequest.of(0, limit + 1)
        );

        boolean hasMore = !result.isLast();

        HistoryCursor nextCursor = null;

        if (hasMore && !result.isEmpty()) {
            Message oldestMessage =
                    result.getContent().get(result.getNumberOfElements() - 1);

            nextCursor = new HistoryCursor(
                    oldestMessage.getCreatedAt(),
                    oldestMessage.getId()
            );
        }
        List<Message> selectedMessages =
                new ArrayList<>(result.getContent());
        Collections.reverse(selectedMessages);

        List<PublishedChatMessage> messages =
                selectedMessages.stream()
                        .map(MessageMapper::toPublishedMessage)
                        .toList();

        return new MessageHistoryResponse(
                messages,
                nextCursor,
                hasMore
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MessageHistoryResponse getBroadcastHistory(LocalDateTime beforeCreatedAt, Long beforeMessageId, int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "limit must be between 1 and 100"
            );
        }


        Page<Message> result = messageRepository.findBroadcastHistory(
                beforeCreatedAt,
                beforeMessageId,
                PageRequest.of(0, limit )
        );

        boolean hasMore = !result.isLast();

        HistoryCursor nextCursor = null;

        if (hasMore && !result.isEmpty()) {
            Message oldestMessage =
                    result.getContent().get(result.getNumberOfElements() - 1);

            nextCursor = new HistoryCursor(
                    oldestMessage.getCreatedAt(),
                    oldestMessage.getId()
            );
        }
        List<Message> selectedMessages =
                new ArrayList<>(result.getContent());
        Collections.reverse(selectedMessages);

        List<PublishedChatMessage> messages =
                selectedMessages.stream()
                        .map(MessageMapper::toPublishedMessage)
                        .toList();

        return new MessageHistoryResponse(
                messages,
                nextCursor,
                hasMore
        );
    }
}
