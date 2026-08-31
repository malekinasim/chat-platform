package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.dto.UnreadMessageCount;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageReceiverRepository extends JpaRepository<MessageReceiver, Long> {
    Optional<MessageReceiver> findByMessage_IdAndReceiverId(Long messageId, String receiverId);

    @Query("""
            select receiver
            from MessageReceiver receiver
            join fetch receiver.message message
            where receiver.receiverId = :receiverId
              and receiver.receiverStatus in (
                  com.nasim.chat.client.model.entity.ReceiverStatus.PENDING,
                  com.nasim.chat.client.model.entity.ReceiverStatus.SENT
              )
              and message.deliveryType = com.nasim.chat.model.dto.DeliveryType.PRIVATE
            order by message.createdAt asc, message.id asc
            """)
    List<MessageReceiver> findReplayablePrivateMessages(@Param("receiverId") String receiverId);


    @Query("""
            select new com.nasim.chat.client.model.dto.UnreadMessageCount(
                case
                    when :deliveryType =
                        com.nasim.chat.model.dto.DeliveryType.GROUP
                    then message.destinationId
                    else message.senderId
                end,
                count(receiver.id)
            )
            from MessageReceiver receiver
            join receiver.message message
            where receiver.receiverId = :receiverId
              and receiver.receiverStatus <>
                  com.nasim.chat.client.model.entity.ReceiverStatus.READ
              and message.deliveryType = :deliveryType
            group by
                case
                    when :deliveryType =
                        com.nasim.chat.model.dto.DeliveryType.GROUP
                    then message.destinationId
                    else message.senderId
                end
            """)
    List<UnreadMessageCount> findUnreadCountsByReceiverId(
            @Param("receiverId") String receiverId,
            @Param("deliveryType") DeliveryType deliveryType
    );
}