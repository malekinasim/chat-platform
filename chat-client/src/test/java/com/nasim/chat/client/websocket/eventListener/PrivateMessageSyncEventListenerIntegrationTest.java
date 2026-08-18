package com.nasim.chat.client.websocket.eventListener;


import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.socket.listener.IncomingMessageDispatcher;
import com.nasim.chat.client.websocket.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.Principal;

public class PrivateMessageSyncEventListenerIntegrationTest {


    private PrivateMessageSyncRegistry registry = new PrivateMessageSyncRegistry();


    private MessageReceiverService messageReceiverService = Mockito.mock(MessageReceiverService.class);

    private IncomingMessageDispatcher messageDispatcher = Mockito.mock(IncomingMessageDispatcher.class);

    private PrivateMessageSyncEventListener listener = new PrivateMessageSyncEventListener(
            registry, messageReceiverService, messageDispatcher);

    @Test
    void authenticatedConnectedSessionIsRegisteredAsNotStarted() {
        Principal principal = () -> "user-1";

        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(StompCommand.CONNECTED);

        accessor.setSessionId("session-1");
        accessor.setUser(principal);

        Message<byte[]> message = MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );

        SessionConnectedEvent event =
                new SessionConnectedEvent(this, message, principal);

        listener.handleConnected(event);
        assertThat(registry.status("session-1"))
                .contains(PrivateMessageSyncStatus.NOT_STARTED);

    }
    @Test
    void notAuthenticatedConnectedSessionIsNotRegistered() {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(StompCommand.CONNECTED);

        accessor.setSessionId("session-1");
        Message<byte[]> message = MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );

        SessionConnectedEvent event =
                new SessionConnectedEvent(this, message, null);

        listener.handleConnected(event);
        assertThat(registry.status("session-1")).isEmpty();


    }

    @Test
    void authenticatedSubscribedSessionIsRegisteredAsInProgress() {
        Principal principal = () -> "user-1";

        StompHeaderAccessor accessor =
                StompHeaderAccessor.create(StompCommand.CONNECTED);

        accessor.setSessionId("session-1");
        accessor.setUser(principal);

        Message<byte[]> message = MessageBuilder.createMessage(
                new byte[0],
                accessor.getMessageHeaders()
        );

        SessionConnectedEvent connectedEvent =
                new SessionConnectedEvent(this, message, principal);

        listener.handleConnected(connectedEvent);

        listener.handlePrivateSubscriptionReady(new PrivateSubscriptionReadyEvent(
                principal.getName(),
                "session-1"
        ));

        assertThat(registry.status("session-1"))
                .contains(PrivateMessageSyncStatus.IN_PROGRESS);

    }

}
