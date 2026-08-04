package com.nasim.chat.client.service;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;

import java.util.List;

public interface MessageReceiverService {

    void saveReceivers(Message message, List<String> receivers);

    default MessageReceiver createReceiver(
            Message message,
            String receiverId
    ) {
        MessageReceiver receiver = new MessageReceiver();
        receiver.setMessage(message);
        receiver.setReceiverId(receiverId);
        receiver.setReceiverStatus(ReceiverStatus.PENDING);
        return receiver;
    }

    void markAsSent(Long messageId);

    void markAsDelivered(Long messageId, String receiverId);

    void markAsRead(Long aLong, String receiverId);
}