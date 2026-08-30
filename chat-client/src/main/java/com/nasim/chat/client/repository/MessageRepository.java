package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
        select message
        from Message message
        where message.deliveryType =
            com.nasim.chat.model.dto.DeliveryType.PRIVATE
          and (
                message.senderId = :userId
                or message.destinationId = :userId
          )
          and (
                (
                    :beforeCreatedAt is null
                    and :beforeMessageId is null
                )
                or
                (
                    :beforeCreatedAt is not null
                    and :beforeMessageId is null
                    and message.createdAt < :beforeCreatedAt
                )
                or
                (
                    :beforeCreatedAt is null
                    and :beforeMessageId is not null
                    and message.id < :beforeMessageId
                )
                or
                (
                    :beforeCreatedAt is not null
                    and :beforeMessageId is not null
                    and (
                        message.createdAt < :beforeCreatedAt
                        or (
                            message.createdAt = :beforeCreatedAt
                            and message.id < :beforeMessageId
                        )
                    )
                )
          )
        order by message.createdAt desc, message.id desc
        """)
    List<Message> findPrivateHistory(
            @Param("userId") String userId,
            @Param("beforeCreatedAt") LocalDateTime beforeCreatedAt,
            @Param("beforeMessageId") Long beforeMessageId,
            Pageable pageable
    );
}