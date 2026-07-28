package com.nasim.chat.client.handler;

import com.nasim.chat.client.model.dto.ChatMessage;
import com.nasim.chat.client.model.dto.DeliveryType;

public interface WebSocketDeliveryHandler extends Handler<DeliveryType>{
    DeliveryType supportedType();
    void deliver(ChatMessage message);
}
