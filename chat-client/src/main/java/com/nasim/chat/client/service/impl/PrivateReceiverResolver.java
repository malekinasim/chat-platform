package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.ReceiverResolver;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrivateReceiverResolver implements ReceiverResolver {
    private final UserDirectoryClient userDirectoryClient;

    public PrivateReceiverResolver(UserDirectoryClient userDirectoryClient) {
        this.userDirectoryClient = userDirectoryClient;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.PRIVATE;
    }

    @Override
    public List<String> resolveReceiverIds(SendMessageCommand command) {
        if(userDirectoryClient.isUserActive(command.receiver()))
          return List.of(command.receiver());
        return new ArrayList<>();
    }
}
