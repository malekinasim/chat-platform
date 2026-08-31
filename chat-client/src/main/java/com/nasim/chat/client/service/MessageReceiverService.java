package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.UnreadMessageCount;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;
import com.nasim.chat.model.dto.PublishedChatMessage;

import java.util.List;

public interface MessageReceiverService {

    void saveReceivers(Message message, List<String> receivers);

    @Deprecated(forRemoval = false)
    List<PublishedChatMessage> getMissedPrivateMessages(String receiverId);

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

    void markAsSent(Long messageId, String receiverId);

    void markAsDelivered(Long messageId, String receiverId);

    void markAsRead(Long aLong, String receiverId);
    List<UnreadMessageCount> getPrivateUnreadCounts(
            String receiverId
    );

    List<UnreadMessageCount> getGroupUnreadCounts(String userId);

    List<UnreadMessageCount> getBroadcastUnreadCounts(String userId);
}
