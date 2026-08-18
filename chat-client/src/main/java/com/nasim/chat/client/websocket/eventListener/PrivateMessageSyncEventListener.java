package com.nasim.chat.client.websocket.eventListener;

import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.socket.listener.IncomingMessageDispatcher;
import com.nasim.chat.client.websocket.WebSocketConfig;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class PrivateMessageSyncEventListener {
     private final PrivateMessageSyncRegistry privateMessageSyncRegistry;
     private final MessageReceiverService messageReceiverService;
     private final IncomingMessageDispatcher messageDispatcher;
    public PrivateMessageSyncEventListener(PrivateMessageSyncRegistry privateMessageSyncRegistry, MessageReceiverService messageReceiverService, IncomingMessageDispatcher messageDispatcher) {
        this.privateMessageSyncRegistry = privateMessageSyncRegistry;
        this.messageReceiverService = messageReceiverService;
        this.messageDispatcher = messageDispatcher;
    }

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();

        if (sessionId == null || principal == null) {
            return;
        }

        String userId = principal.getName();

        if (userId == null || userId.isBlank()) {
            return;
        }

        privateMessageSyncRegistry.registerSession(userId, sessionId);
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();
        String destination=accessor.getDestination();
        if (sessionId == null || principal == null) {
            return;
        }

        String userId = principal.getName();

        if (userId == null || userId.isBlank()) {
            return;
        }
        if (destination == null ||
                !destination.equals(WebSocketConfig.PRIVATE_TOPIC_PREFIX)) {
            return;
        }
        if (!privateMessageSyncRegistry.tryStartSessionSync(userId, sessionId)) {
            return;
        }
        CompletableFuture<Void> syncFuture =  privateMessageSyncRegistry.runOrJoinUserSync(userId,
                ()-> CompletableFuture.runAsync(
                        ()-> this.syncPrivateMessages(userId)
                  )
                );
        syncFuture.whenComplete((ignored, error) -> {
            if (error == null) {
                privateMessageSyncRegistry.completeSessionSync(sessionId);
            } else {
                privateMessageSyncRegistry.resetSessionSync(sessionId);
            }
        });
    }

    @EventListener
    private void handelDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        privateMessageSyncRegistry.removeSession(sessionId);

    }
    @EventListener
    private void handelDisconnect(SessionUnsubscribeEvent event){
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        privateMessageSyncRegistry.removeSession(sessionId);

    }
    private void syncPrivateMessages(String userId) {
        List<PublishedChatMessage> publishedChatMessages= messageReceiverService.getMissedPrivateMessages(userId);
        publishedChatMessages.forEach(
                messageDispatcher::dispatch
        );
    }


}
