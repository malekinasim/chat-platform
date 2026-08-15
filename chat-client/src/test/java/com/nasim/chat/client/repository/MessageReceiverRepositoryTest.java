package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.MessageContentType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageReceiverRepositoryTest {

    @Autowired
    private MessageReceiverRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findsOnlyPendingPrivateMessagesForReceiverInCreationOrderThenIdOrder() {
        MessageReceiver later = persistReceiver("target", ReceiverStatus.PENDING, DeliveryType.PRIVATE, "later");
        MessageReceiver firstAtSameTime = persistReceiver("target", ReceiverStatus.PENDING, DeliveryType.PRIVATE, "first-at-same-time");
        MessageReceiver secondAtSameTime = persistReceiver("target", ReceiverStatus.PENDING, DeliveryType.PRIVATE, "second-at-same-time");

        persistReceiver("someone-else", ReceiverStatus.PENDING, DeliveryType.PRIVATE, "wrong receiver");
        persistReceiver("target", ReceiverStatus.SENT, DeliveryType.PRIVATE, "wrong status");
        persistReceiver("target", ReceiverStatus.PENDING, DeliveryType.GROUP, "group");
        persistReceiver("target", ReceiverStatus.PENDING, DeliveryType.BROADCAST, "broadcast");

        entityManager.flush();
        setMessageCreationTime(later, LocalDateTime.of(2026, 1, 2, 12, 0));
        setMessageCreationTime(firstAtSameTime, LocalDateTime.of(2026, 1, 1, 12, 0));
        setMessageCreationTime(secondAtSameTime, LocalDateTime.of(2026, 1, 1, 12, 0));
        entityManager.clear();

        List<MessageReceiver> result = repository.findPendingPrivateMessages("target");

        assertThat(result)
                .extracting(receiver -> receiver.getMessage().getTextContent())
                .containsExactly("first-at-same-time", "second-at-same-time", "later");
    }

    @Test
    void returnsEmptyListWhenReceiverHasNoPendingPrivateMessages() {
        persistReceiver("target", ReceiverStatus.READ, DeliveryType.PRIVATE, "already read");

        assertThat(repository.findPendingPrivateMessages("target")).isEmpty();
    }

    private MessageReceiver persistReceiver(
            String receiverId,
            ReceiverStatus status,
            DeliveryType deliveryType,
            String content
    ) {
        Message message = new Message();
        message.setSenderId("sender");
        message.setTextContent(content);
        message.setMessageContentType(MessageContentType.TEXT);
        message.setDeliveryType(deliveryType);
        message.setDestinationId(receiverId);
        entityManager.persist(message);

        MessageReceiver receiver = new MessageReceiver();
        receiver.setMessage(message);
        receiver.setReceiverId(receiverId);
        receiver.setReceiverStatus(status);
        entityManager.persist(receiver);
        return receiver;
    }

    private void setMessageCreationTime(MessageReceiver receiver, LocalDateTime createdAt) {
        entityManager.createNativeQuery("update message set created_at = :createdAt where id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", receiver.getMessage().getId())
                .executeUpdate();
    }
}
