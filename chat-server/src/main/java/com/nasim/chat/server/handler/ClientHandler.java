package com.nasim.chat.server.handler;

import com.nasim.chat.server.client.ClientConnection;

public interface ClientHandler {
    void handle(ClientConnection client);
}
