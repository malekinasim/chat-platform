package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.UnreadMessageCount;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.repository.MessageReceiverRepository;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.MessageContentType;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageReceiverServiceImplTest {

    @Mock
    private MessageReceiverRepository repository;

    @InjectMocks
    private MessageReceiverServiceImpl service;

    @Test
    void mapsStoredMessagesToPublishedChatMessages() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 3, 4, 5);
        Message message = new Message();
        message.setId(42L);
        message.setCreatedAt(createdAt);
        message.setDeliveryType(DeliveryType.PRIVATE);
        message.setMessageContentType(MessageContentType.TEXT);
        message.setSenderId("sender-id");
        message.setTextContent("missed message");

        MessageReceiver receiver = new MessageReceiver();
        receiver.setMessage(message);
        receiver.setReceiverId("receiver-id");
        when(repository.findReplayablePrivateMessages("receiver-id")).thenReturn(List.of(receiver));

        List<PublishedChatMessage> result = service.getMissedPrivateMessages("receiver-id");

        assertThat(result).containsExactly(new PublishedChatMessage(
                DeliveryType.PRIVATE,
                MessageContentType.TEXT,
                "sender-id",
                "receiver-id",
                "missed message",
                null,
                42L,
                createdAt
        ));
        verify(repository).findReplayablePrivateMessages("receiver-id");
    }

    @Test
    void returnsEmptyListWhenRepositoryHasNoMessages() {
        when(repository.findReplayablePrivateMessages("receiver-id")).thenReturn(List.of());

        assertThat(service.getMissedPrivateMessages("receiver-id")).isEmpty();
        verify(repository).findReplayablePrivateMessages("receiver-id");
    }
    @Test
    void returnsPrivateUnreadCounts() {
        List<UnreadMessageCount> counts = List.of(
                new UnreadMessageCount("sender-a", 3L),
                new UnreadMessageCount("sender-b", 1L)
        );

        when(repository.findUnreadCountsByReceiverId("receiver-id",DeliveryType.PRIVATE))
                .thenReturn(counts);

        assertThat(service.getPrivateUnreadCounts("receiver-id"))
                .containsExactlyElementsOf(counts);

        verify(repository)
                .findUnreadCountsByReceiverId("receiver-id",DeliveryType.PRIVATE);
    }
}
