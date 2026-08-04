package com.nasim.chat.client.websocket.channelInterceptor;
import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.model.dto.PublishedChatMessage;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

public class OutboundMessageInterceptor implements ChannelInterceptor {
    private final MessageReceiverService messageReceiverService;

    public OutboundMessageInterceptor(MessageReceiverService messageReceiverService) {
        this.messageReceiverService = messageReceiverService;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception exception) {

        if (sent && exception == null) {
            Object payload = message.getPayload();

            if (payload instanceof PublishedChatMessage publishedMessage) {
                Long messageId = publishedMessage.messageId();
                messageReceiverService.markAsSent(messageId);
            }



        }
    }
}
