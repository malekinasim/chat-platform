package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.ReceiverResolver;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class PublicReceiverResolver implements ReceiverResolver {
    private final UserDirectoryClient userDirectoryClient;

    public PublicReceiverResolver(UserDirectoryClient userDirectoryClient) {
        this.userDirectoryClient = userDirectoryClient;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.BROADCAST;
    }

    @Override
    public List<String> resolveReceiverIds(SendMessageCommand command,
                                           JwtAuthenticationToken authentication ) {
        return userDirectoryClient.findAllActiveUserIds();
    }
}
