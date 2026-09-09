package com.nasim.chat.server.listener;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.server.client.ClientConnection;
import com.nasim.chat.server.client.ClientRegistryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessageListenerImpl implements MessageListener{
    private final ClientRegistryService clientRegistryService;

    public MessageListenerImpl(@Lazy ClientRegistryService clientRegistryService) {
        this.clientRegistryService = clientRegistryService;
    }


    @Override
    public void dispatch(
            PublishedChatMessage message,
            ClientConnection sourceClient
    ) {
        try {
            sourceClient.send(message);
        } catch (IOException exception) {
            clientRegistryService.unregister(sourceClient);

            throw new IllegalStateException(
                    "Could not return message to source client",
                    exception
            );
        }
    }
}
