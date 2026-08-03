package com.nasim.chat.client.socket.listener;

import com.nasim.chat.client.handler.Handler;
import com.nasim.chat.client.handler.IncomingContentHandler;
import com.nasim.chat.client.handler.WebSocketDeliveryHandler;
import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.MessageContentType;
import com.nasim.chat.model.dto.DeliveryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class IncomingMessageDispatcher
        implements MessageListener {

    private final Map<MessageContentType, IncomingContentHandler> contentHandlers;
    private final Map<DeliveryType, WebSocketDeliveryHandler>
            deliveryHandlers;

    public IncomingMessageDispatcher(
            List<IncomingContentHandler> incomingContentHandlerList,
            List<WebSocketDeliveryHandler> deliveryHandlerList
    ) {
        this.contentHandlers =
                buildHandlerMap(incomingContentHandlerList);

        this.deliveryHandlers =
                buildHandlerMap(deliveryHandlerList);
    }

    @Override
    public void dispatch(PublishedChatMessage message) {
        IncomingContentHandler incomingContentHandler =
                contentHandlers.get(message.messageContentType());

        if (incomingContentHandler == null) {
            throw new IllegalArgumentException(
                    "Unsupported content type: "
                            + message.messageContentType()
            );
        }

        WebSocketDeliveryHandler deliveryHandler =
                deliveryHandlers.get(message.deliveryType());

        if (deliveryHandler == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser delivery type: "
                            + message.deliveryType()
            );
        }

        PublishedChatMessage processedMessage =
                incomingContentHandler.handle(message);

        deliveryHandler.deliver(processedMessage);
    }

    private static <
            T extends Enum<T>,
            H extends Handler<T>
            > Map<T, H> buildHandlerMap(List<H> handlers) {

        return handlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Handler::supportedType,
                        Function.identity()
                ));
    }
}