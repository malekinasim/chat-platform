package com.nasim.chat.client.websocket.channelInterceptor;

import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Optional;

public class OutboundMessageInterceptor implements ChannelInterceptor {
    private final MessageReceiverService messageReceiverService;
    private final SimpUserRegistry userRegistry;
    public OutboundMessageInterceptor(MessageReceiverService messageReceiverService, SimpUserRegistry userRegistry) {
        this.messageReceiverService = messageReceiverService;
        this.userRegistry = userRegistry;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception exception) {
        if (!sent || exception != null) return;
        StompHeaderAccessor headerAccessor=MessageHeaderAccessor.getAccessor( message,
                StompHeaderAccessor.class);
        if(headerAccessor==null || headerAccessor.getCommand()==null || headerAccessor.getCommand()!= StompCommand.MESSAGE) return;

        String sessionId= headerAccessor.getSessionId();
        if(sessionId==null) return;
        findUsernameBySessionId(sessionId).ifPresent(
                username->{
                 Long messageID=  this.extractMessageId(message);
                 this.messageReceiverService.markAsSent(messageID,username);
                }
        );
    }

    private Long extractMessageId(Message<?> message) {
        Object messageId = message.getHeaders().get("chatMessageId");

        if (messageId instanceof Long id) {
            return id;
        }

        if (messageId instanceof Number number) {
            return number.longValue();
        }

        return null;
    }

    private Optional<String> findUsernameBySessionId(String sessionId) {
       return userRegistry.getUsers().stream().filter(
                user->user.getSessions().stream().anyMatch(
                        simpSession -> simpSession.getId().equals(sessionId)
                )
        ).map(SimpUser::getName).findFirst();
    }

}
