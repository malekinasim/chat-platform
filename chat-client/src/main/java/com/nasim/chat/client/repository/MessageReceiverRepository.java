package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.MessageReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReceiverRepository extends JpaRepository<MessageReceiver, Long> {
    @Query(value = """
            select msgr from MessageReceiver msgr
            join msgr.message msg
            where msgr.receiverStatus=ReciverStatus.PENDING
            and msg.id= :messageId
            """)
    List<MessageReceiver> findByMessageId(@Param("messageId") Long messageId);
    Optional<MessageReceiver> findByMessage_IdAndReceiverId(Long messageId, String receiverId);
}
