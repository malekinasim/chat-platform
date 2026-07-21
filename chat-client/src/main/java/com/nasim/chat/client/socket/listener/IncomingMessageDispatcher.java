package com.nasim.chat.client.socket.listener;

import com.nasim.chat.client.handler.IncomingContentHandler;
import com.nasim.chat.client.handler.WebSocketDeliveryHandler;
import com.nasim.chat.client.handler.Handler;
import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.ContentType;
import com.nasim.chat.model.DeliveryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class IncomingMessageDispatcher
        implements MessageListener {

    private final Map<ContentType, IncomingContentHandler> contentHandlers;
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
    public void dispatch(ChatMessage message) {
        IncomingContentHandler incomingContentHandler =
                contentHandlers.get(message.contentType());

        if (incomingContentHandler == null) {
            throw new IllegalArgumentException(
                    "Unsupported content type: "
                            + message.contentType()
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

        ChatMessage processedMessage =
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