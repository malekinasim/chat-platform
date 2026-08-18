package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.socket.listener.IncomingMessageDispatcher;
import com.nasim.chat.client.websocket.WebSocketConfig;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateMessageSyncEventListenerTest {

    @Mock
    private PrivateMessageSyncRegistry registry;

    @Mock
    private MessageReceiverService messageReceiverService;

    @Mock
    private IncomingMessageDispatcher messageDispatcher;

    @InjectMocks
    private PrivateMessageSyncEventListener listener;

    @Test
    void authenticatedConnectedSessionIsRegisteredWithAuthenticatedUserId() {
        listener.handleConnected(connectedEvent("session-1", () -> "user-1"));

        verify(registry).registerSession("user-1", "session-1");
        verifyNoInteractions(messageReceiverService, messageDispatcher);
    }

    @Test
    void connectedEventsWithIncompleteAuthenticationDataAreIgnored() {
        listener.handleConnected(connectedEvent(null, () -> "user-1"));
        listener.handleConnected(connectedEvent("missing-principal", null));
        listener.handleConnected(connectedEvent("null-name", () -> null));
        listener.handleConnected(connectedEvent("blank-name", () -> " \t "));

        verifyNoInteractions(registry, messageReceiverService, messageDispatcher);
    }

    @Test
    void onlyExactPrivateDestinationCanStartReplay() {
        Principal principal = () -> "user-1";

        listener.handleSubscribe(subscribeEvent("session-1", principal, null));
        listener.handleSubscribe(subscribeEvent("session-1", principal, "/user/queue"));
        listener.handleSubscribe(subscribeEvent("session-1", principal,
                WebSocketConfig.PRIVATE_TOPIC_PREFIX + "/extra"));
        listener.handleSubscribe(subscribeEvent("session-1", principal,
                WebSocketConfig.PRIVATE_TOPIC_PREFIX + "-similar"));
        listener.handleSubscribe(subscribeEvent("session-1", principal,
                WebSocketConfig.ROOM_TOPIC_PREFIX + "room-1"));

        verifyNoInteractions(registry, messageReceiverService, messageDispatcher);
    }

    @Test
    void validSubscriptionDoesNotRunUserOperationWhenSessionTransitionFails() {
        when(registry.tryStartSessionSync("user-1", "session-1")).thenReturn(false);

        listener.handleSubscribe(privateSubscribeEvent("session-1", () -> "user-1"));

        verify(registry).tryStartSessionSync("user-1", "session-1");
        verify(registry, never()).runOrJoinUserQuery(any(), any());
        verifyNoInteractions(messageReceiverService, messageDispatcher);
    }

    @Test
    void validSubscriptionStartsOrJoinsUserOperationAndReplaysEveryMessage() {
        when(registry.tryStartSessionSync("user-1", "session-1")).thenReturn(true);
        when(registry.runOrJoinUserQuery(any(), any())).thenAnswer(invocation -> {
            Supplier<CompletableFuture<List<PublishedChatMessage>>> query =
                    invocation.getArgument(1);
            return query.get();
        });
        PublishedChatMessage first = org.mockito.Mockito.mock(PublishedChatMessage.class);
        PublishedChatMessage second = org.mockito.Mockito.mock(PublishedChatMessage.class);
        when(messageReceiverService.getMissedPrivateMessages("user-1"))
                .thenReturn(List.of(first, second));

        listener.handleSubscribe(privateSubscribeEvent("session-1", () -> "user-1"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Supplier<CompletableFuture<List<PublishedChatMessage>>>> operation =
                ArgumentCaptor.forClass(Supplier.class);
        verify(registry).runOrJoinUserQuery(org.mockito.ArgumentMatchers.eq("user-1"),
                operation.capture());
        verify(messageReceiverService).getMissedPrivateMessages("user-1");
        verify(messageDispatcher, timeout(1_000)).dispatch(first);
        verify(messageDispatcher, timeout(1_000)).dispatch(second);
        verifyNoMoreInteractions(messageReceiverService, messageDispatcher);
    }

    @Test
    void sessionsSharingAQueryEachDispatchTheEntireResult() {
        CompletableFuture<List<PublishedChatMessage>> sharedQuery = new CompletableFuture<>();
        PublishedChatMessage first = org.mockito.Mockito.mock(PublishedChatMessage.class);
        PublishedChatMessage second = org.mockito.Mockito.mock(PublishedChatMessage.class);
        when(registry.tryStartSessionSync("user-1", "session-1")).thenReturn(true);
        when(registry.tryStartSessionSync("user-1", "session-2")).thenReturn(true);
        when(registry.runOrJoinUserQuery(any(), any())).thenReturn(sharedQuery);

        listener.handleSubscribe(privateSubscribeEvent("session-1", () -> "user-1"));
        listener.handleSubscribe(privateSubscribeEvent("session-2", () -> "user-1"));
        sharedQuery.complete(List.of(first, second));

        verify(messageDispatcher, timeout(1_000).times(2)).dispatch(first);
        verify(messageDispatcher, timeout(1_000).times(2)).dispatch(second);
        verify(registry, timeout(1_000)).completeSessionSync("session-1");
        verify(registry, timeout(1_000)).completeSessionSync("session-2");
        verify(registry, times(2)).runOrJoinUserQuery(
                org.mockito.ArgumentMatchers.eq("user-1"), any());
        verifyNoInteractions(messageReceiverService);
    }

    @Test
    void successfulCompletionCompletesOnlyParticipatingSession() {
        CompletableFuture<List<PublishedChatMessage>> joinedFuture = preparedSuccessfulTransition("session-1");

        listener.handleSubscribe(privateSubscribeEvent("session-1", () -> "user-1"));
        verify(registry, never()).completeSessionSync(any());

        joinedFuture.complete(List.of());

        verify(registry, timeout(1_000)).completeSessionSync("session-1");
        verify(registry, never()).completeSessionSync("session-2");
        verify(registry, never()).resetSessionSync(any());
    }

    @Test
    void exceptionalCompletionResetsOnlyParticipatingSession() {
        CompletableFuture<List<PublishedChatMessage>> joinedFuture = preparedSuccessfulTransition("session-1");

        listener.handleSubscribe(privateSubscribeEvent("session-1", () -> "user-1"));
        joinedFuture.completeExceptionally(new IllegalStateException("replay failed"));

        verify(registry).resetSessionSync("session-1");
        verify(registry, never()).resetSessionSync("session-2");
        verify(registry, never()).completeSessionSync(any());
    }

    @Test
    void disconnectRemovesOnlyDisconnectedSession() {
        SessionDisconnectEvent event = new SessionDisconnectEvent(
                this, message(StompCommand.DISCONNECT, "session-1", null, null),
                "session-1", CloseStatus.NORMAL);

        ReflectionTestUtils.invokeMethod(listener, "handelDisconnect", event);

        verify(registry).removeSession("session-1");
        verify(registry, never()).removeSession("session-2");
        verifyNoInteractions(messageReceiverService, messageDispatcher);
    }

    private CompletableFuture<List<PublishedChatMessage>> preparedSuccessfulTransition(
            String sessionId) {
        CompletableFuture<List<PublishedChatMessage>> joinedFuture = new CompletableFuture<>();
        when(registry.tryStartSessionSync("user-1", sessionId)).thenReturn(true);
        when(registry.runOrJoinUserQuery(any(), any())).thenReturn(joinedFuture);
        return joinedFuture;
    }

    private SessionConnectedEvent connectedEvent(String sessionId, Principal principal) {
        return new SessionConnectedEvent(
                this, message(StompCommand.CONNECTED, sessionId, principal, null), principal);
    }

    private SessionSubscribeEvent privateSubscribeEvent(String sessionId, Principal principal) {
        return subscribeEvent(sessionId, principal, WebSocketConfig.PRIVATE_TOPIC_PREFIX);
    }

    private SessionSubscribeEvent subscribeEvent(
            String sessionId, Principal principal, String destination) {
        return new SessionSubscribeEvent(
                this, message(StompCommand.SUBSCRIBE, sessionId, principal, destination), principal);
    }

    private Message<byte[]> message(
            StompCommand command, String sessionId, Principal principal, String destination) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setSessionId(sessionId);
        accessor.setUser(principal);
        accessor.setDestination(destination);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
