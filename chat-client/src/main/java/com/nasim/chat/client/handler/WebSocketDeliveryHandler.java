package com.nasim.chat.client.handler;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.DeliveryType;

public interface WebSocketDeliveryHandler extends Handler<DeliveryType>{
    DeliveryType supportedType();
    void deliver(ChatMessage message);
}
