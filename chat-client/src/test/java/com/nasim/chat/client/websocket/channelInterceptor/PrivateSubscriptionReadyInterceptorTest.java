package com.nasim.chat.client.websocket.channelInterceptor;

import com.nasim.chat.client.websocket.WebSocketConfig;
import com.nasim.chat.client.websocket.eventListener.PrivateSubscriptionReadyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import java.security.Principal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PrivateSubscriptionReadyInterceptorTest {

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private MessageChannel channel;

    @Mock
    private SimpleBrokerMessageHandler brokerHandler;

    @InjectMocks
    private PrivateSubscriptionReadyInterceptor interceptor;

    @Test
    void successfulPrivateSubscribePublishesReadyEvent() {
        Principal principal = () -> "user-1";

        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.SUBSCRIBE,
                "session-1",
                principal,
                WebSocketConfig.PRIVATE_TOPIC_PREFIX
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                brokerHandler,
                null
        );

        ArgumentCaptor<PrivateSubscriptionReadyEvent> captor =
                ArgumentCaptor.forClass(
                        PrivateSubscriptionReadyEvent.class
                );

        verify(publisher).publishEvent(captor.capture());

        assertThat(captor.getValue())
                .isEqualTo(
                        new PrivateSubscriptionReadyEvent(
                                "session-1",
                                "user-1"
                        )
                );
    }

    @Test
    void nonPrivateSubscriptionDoesNotPublishEvent() {
        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.SUBSCRIBE,
                "session-1",
                () -> "user-1",
                "/user/queue/another-destination"
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                brokerHandler,
                null
        );

        verifyNoInteractions(publisher);
    }

    @Test
    void nonSubscribeMessageDoesNotPublishEvent() {
        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.MESSAGE,
                "session-1",
                () -> "user-1",
                WebSocketConfig.PRIVATE_TOPIC_PREFIX
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                brokerHandler,
                null
        );

        verifyNoInteractions(publisher);
    }

    @Test
    void brokerFailureDoesNotPublishEvent() {
        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.SUBSCRIBE,
                "session-1",
                () -> "user-1",
                WebSocketConfig.PRIVATE_TOPIC_PREFIX
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                brokerHandler,
                new IllegalStateException("broker failed")
        );

        verifyNoInteractions(publisher);
    }

    @Test
    void messageHandledByNonBrokerHandlerDoesNotPublishEvent() {
        MessageHandler otherHandler =
                mock(MessageHandler.class);

        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.SUBSCRIBE,
                "session-1",
                () -> "user-1",
                WebSocketConfig.PRIVATE_TOPIC_PREFIX
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                otherHandler,
                null
        );

        verifyNoInteractions(publisher);
    }

    @Test
    void subscriptionWithoutAuthenticatedUserDoesNotPublishEvent() {
        Message<byte[]> message = brokerSubscribeMessage(
                SimpMessageType.SUBSCRIBE,
                "session-1",
                null,
                WebSocketConfig.PRIVATE_TOPIC_PREFIX
        );

        interceptor.afterMessageHandled(
                message,
                channel,
                brokerHandler,
                null
        );

        verify(
                publisher,
                never()
        ).publishEvent(
                org.mockito.ArgumentMatchers.any()
        );
    }

    private Message<byte[]> brokerSubscribeMessage(
            SimpMessageType messageType,
            String sessionId,
            Principal principal,
            String originalDestination
    ) {
        SimpMessageHeaderAccessor accessor =
                SimpMessageHeaderAccessor.create(messageType);

        accessor.setSessionId(sessionId);
        accessor.setUser(principal);

        // Destination translated by UserDestinationMessageHandler
        accessor.setDestination(
                "/queue/private-user" + sessionId
        );

        accessor.setNativeHeader(
                SimpMessageHeaderAccessor.ORIGINAL_DESTINATION,
                originalDestination
        );

        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
    }
}