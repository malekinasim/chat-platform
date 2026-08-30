package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.UnreadMessageCount;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;
import com.nasim.chat.client.model.entity.mapper.MessageMapper;
import com.nasim.chat.client.repository.MessageReceiverRepository;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class MessageReceiverServiceImpl implements com.nasim.chat.client.service.MessageReceiverService {


    private final MessageReceiverRepository receiverRepository;

    public MessageReceiverServiceImpl(MessageReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }
    @Override
    public void saveReceivers(Message message, List<String> receivers) {
        List<MessageReceiver> receiverList = receivers.stream()
                        .map(memberId -> this.createReceiver(message, memberId))
                        .toList();
        receiverRepository.saveAll(receiverList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublishedChatMessage> getMissedPrivateMessages(String receiverId) {
        return receiverRepository.findReplayablePrivateMessages(receiverId).stream()
                .map(receiver -> MessageMapper.toPublishedMessage(
                        receiver.getMessage(), receiver.getReceiverId()))
                .toList();
    }

    @Override
    @Transactional
    public void markAsSent(Long messageId, String receiverId) {
        MessageReceiver receiver = receiverRepository
                .findByMessage_IdAndReceiverId(messageId, receiverId)
                .orElseThrow(() -> new AccessDeniedException(
                        "The message does not belong to this receiver")
                );

        if (receiver.getReceiverStatus() == ReceiverStatus.PENDING) {

            receiver.setReceiverStatus(ReceiverStatus.SENT);
            receiver.setSendAt(Instant.now());
        }
    }

    @Override
    @Transactional
    public void markAsDelivered(Long messageId, String receiverId) {
        MessageReceiver receiver = receiverRepository
                .findByMessage_IdAndReceiverId(messageId, receiverId)
                .orElseThrow(() -> new AccessDeniedException(
                        "The message does not belong to this receiver")
                );

        if (receiver.getReceiverStatus() == ReceiverStatus.SENT) {

            receiver.setReceiverStatus(ReceiverStatus.DELIVERED);
            receiver.setDeliveredAt(Instant.now());
        }
    }
    @Override
    @Transactional
    public void markAsRead(Long messageId, String receiverId) {
        MessageReceiver receiver = receiverRepository
                .findByMessage_IdAndReceiverId(messageId, receiverId)
                .orElseThrow(() -> new AccessDeniedException(
                        "The message does not belong to this receiver")
                );

        if (receiver.getReceiverStatus() != ReceiverStatus.READ) {

            receiver.setReceiverStatus(ReceiverStatus.READ);
            receiver.setReadAt(Instant.now());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnreadMessageCount> getPrivateUnreadCounts(
            String receiverId
    ) {
        return receiverRepository
                .findPrivateUnreadCountsByReceiverId(receiverId);
    }
}
