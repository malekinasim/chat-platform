package com.nasim.chat.server.listener;

import com.nasim.chat.server.client.ClientConnection;
import com.nasim.chat.server.client.ClientRegistryService;
import com.nasim.chat.model.ChatMessage;
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
    public void dispatch(ChatMessage chatMessage) {
        for (ClientConnection client :  clientRegistryService.getClients()) {
            try {
                client.send(chatMessage);
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
