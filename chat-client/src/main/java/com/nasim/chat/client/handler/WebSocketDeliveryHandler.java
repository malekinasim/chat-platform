package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.DeliveryType;

public interface WebSocketDeliveryHandler extends Handler<DeliveryType>{
    DeliveryType supportedType();
    void deliver(ChatMessage message);
}
