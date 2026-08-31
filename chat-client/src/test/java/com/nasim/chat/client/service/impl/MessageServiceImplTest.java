package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.MessageHistoryResponse;
import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.repository.MessageRepository;
import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.MessageContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageReceiverService messageReceiverService;

    @InjectMocks
    private MessageServiceImpl service;

    @Test
    void returnsRequestedPageOldestFirstWithCursorWhenMoreMessagesExist() {
        Message newest = message(
                3L,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                "newest"
        );

        Message oldestInPage = message(
                2L,
                LocalDateTime.of(2026, 1, 1, 11, 0),
                "oldest-in-page"
        );

        when(messageRepository.findPrivateHistory(
                eq("current"),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(newest, oldestInPage),
                        PageRequest.of(0, 2),
                        3
                )
        );

        MessageHistoryResponse response =
                service.getPrivateHistory(
                        "current",
                        null,
                        null,
                        2
                );

        assertThat(response.messages())
                .extracting(message -> message.messageId())
                .containsExactly(2L, 3L);

        assertThat(response.hasMore()).isTrue();

        assertThat(response.nextCursor().beforeCreatedAt())
                .isEqualTo(oldestInPage.getCreatedAt());

        assertThat(response.nextCursor().beforeMessageId())
                .isEqualTo(oldestInPage.getId());

        verify(messageRepository).findPrivateHistory(
                eq("current"),
                isNull(),
                isNull(),
                argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 2
                )
        );
    }
    @Test
    void returnsNoCursorWhenThereIsNoOlderPage() {
        Message onlyMessage = message(
                1L,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                "only"
        );

        when(messageRepository.findPrivateHistory(
                eq("current"),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(
                        List.of(onlyMessage),
                        PageRequest.of(0, 2),
                        1
                )
        );

        MessageHistoryResponse response =
                service.getPrivateHistory(
                        "current",
                        null,
                        null,
                        2
                );

        assertThat(response.messages())
                .extracting(message -> message.messageId())
                .containsExactly(1L);

        assertThat(response.hasMore()).isFalse();
        assertThat(response.nextCursor()).isNull();

        verify(messageRepository).findPrivateHistory(
                eq("current"),
                isNull(),
                isNull(),
                argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 2
                )
        );
    }
    @Test
    void rejectsInvalidLimit() {
        assertThatThrownBy(
                () -> service.getPrivateHistory(
                        "current", null, null, 0
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit must be between 1 and 100");

        assertThatThrownBy(
                () -> service.getPrivateHistory(
                        "current", null, null, 101
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit must be between 1 and 100");
    }

    private Message message(
            Long id,
            LocalDateTime createdAt,
            String content
    ) {
        Message message = new Message();
        message.setId(id);
        message.setCreatedAt(createdAt);
        message.setSenderId("other");
        message.setDestinationId("current");
        message.setDeliveryType(DeliveryType.PRIVATE);
        message.setMessageContentType(MessageContentType.TEXT);
        message.setTextContent(content);
        return message;
    }
}