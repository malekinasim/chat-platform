package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.socket.listener.IncomingMessageDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PrivateMessageSyncEventListenerIntegrationTest {

    private PrivateMessageSyncRegistry registry;
    private MessageReceiverService messageReceiverService;
    private IncomingMessageDispatcher messageDispatcher;
    private PrivateMessageSyncEventListener listener;
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        registry = Mockito.spy(new PrivateMessageSyncRegistry());

        messageReceiverService =
                Mockito.mock(MessageReceiverService.class);

        messageDispatcher =
                Mockito.mock(IncomingMessageDispatcher.class);

        listener = new PrivateMessageSyncEventListener(
                registry,
                messageReceiverService,
                messageDispatcher
        );

        context = new AnnotationConfigApplicationContext();

        context.registerBean(
                PrivateMessageSyncEventListener.class,
                () -> listener
        );

        context.refresh();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    void authenticatedConnectedEventRegistersSessionAsNotStarted() {
        Principal principal = () -> "user-1";

        context.publishEvent(
                connectedEvent("session-1", principal)
        );

        assertThat(registry.status("session-1"))
                .contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    @Test
    void unauthenticatedConnectedEventDoesNotRegisterSession() {
        context.publishEvent(
                connectedEvent("session-1", null)
        );

        assertThat(registry.status("session-1"))
                .isEmpty();
    }

    @Test
    void privateSubscriptionReadyEventStartsReplay() {
        Principal principal = () -> "user-1";

        Mockito.when(
                messageReceiverService
                        .getMissedPrivateMessages("user-1")
        ).thenReturn(List.of());

        context.publishEvent(
                connectedEvent("session-1", principal)
        );

        context.publishEvent(
                new PrivateSubscriptionReadyEvent(
                        "session-1",
                        "user-1"
                )
        );

        /*
         * This call happens synchronously inside the event listener.
         */
        Mockito.verify(registry)
                .tryStartSessionSync(
                        "user-1",
                        "session-1"
                );

        /*
         * The database query runs through CompletableFuture.runAsync,
         * therefore the verification waits for it.
         */
        Mockito.verify(
                messageReceiverService,
                Mockito.timeout(1000)
        ).getMissedPrivateMessages("user-1");
    }

    @Test
    void readyEventWithoutRegisteredSessionDoesNotStartReplay() {
        context.publishEvent(
                new PrivateSubscriptionReadyEvent(
                        "session-1",
                        "user-1"
                )
        );

        Mockito.verify(registry)
                .tryStartSessionSync(
                        "user-1",
                        "session-1"
                );

        Mockito.verifyNoInteractions(
                messageReceiverService,
                messageDispatcher
        );
    }

    @Test
    void disconnectEventRemovesOnlyDisconnectedSession() {
        Principal principal = () -> "user-1";

        context.publishEvent(
                connectedEvent("session-1", principal)
        );

        context.publishEvent(
                connectedEvent("session-2", principal)
        );

        context.publishEvent(
                disconnectEvent("session-1")
        );

        assertThat(registry.status("session-1"))
                .isEmpty();

        assertThat(registry.status("session-2"))
                .contains(PrivateMessageSyncStatus.NOT_STARTED);
    }

    private SessionConnectedEvent connectedEvent(
            String sessionId,
            Principal principal
    ) {
        Message<byte[]> message = message(
                StompCommand.CONNECTED,
                sessionId,
                principal
        );

        return new SessionConnectedEvent(
                this,
                message,
                principal
        );
    }

    private SessionDisconnectEvent disconnectEvent(
            String sessionId
    ) {
        return new SessionDisconnectEvent(
                this,
                message(
                        StompCommand.DISCONNECT,
                        sessionId,
                        null
                ),
                sessionId,
                CloseStatus.NORMAL
        );
    }

    private Message<byte[]> message(
            StompCommand command,
            String sessionId,
            Principal principal
    ) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(command);

        accessor.setSessionId(sessionId);

        if (principal != null) {
            accessor.setUser(principal);
        }

        return MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );
    }
}