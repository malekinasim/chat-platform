package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.ReceiverResolver;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.exception.RecipientNotFoundException;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

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
    public List<String> resolveReceiverIds(SendMessageCommand command,JwtAuthenticationToken authentication) {
        String accessToken =
                authentication.getToken().getTokenValue();
        if (!userDirectoryClient.userExists(command.receiver(),accessToken)) {
            throw new RecipientNotFoundException(command.receiver());
        }

        return List.of(command.receiver());
    }
}
