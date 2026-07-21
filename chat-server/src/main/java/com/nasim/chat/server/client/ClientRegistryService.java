package com.nasim.chat.server.client;

import java.nio.channels.SocketChannel;
import java.util.List;

public interface ClientRegistryService {

    void register(SocketChannel socketChannel);

    void unregister(ClientConnection client);

    List<ClientConnection> getClients();
}