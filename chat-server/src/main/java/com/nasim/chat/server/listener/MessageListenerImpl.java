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
    public void dispatch(PublishedChatMessage publishedChatMessage) {
        for (ClientConnection client :  clientRegistryService.getClients()) {
            try {
                client.send(publishedChatMessage);
            } catch (IOException e) {
                clientRegistryService.unregister(client);

                System.out.println(
                        "Could not send message to "
                                + client.getInetSocketAddress()
                                + ": "
                                + e.getMessage()
                );
            }
        }
    }
}
