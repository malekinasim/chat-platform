package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReceiverRepository extends JpaRepository<MessageReceiver, Long> {
    Optional<MessageReceiver> findByMessage_IdAndReceiverId(Long messageId, String receiverId);
}
