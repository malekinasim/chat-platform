package com.nasim.chat.client.service;

import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import java.util.List;

public interface ReceiverResolver {
    DeliveryType supportedType();
    List<String> resolveReceiverIds(SendMessageCommand command);
}
