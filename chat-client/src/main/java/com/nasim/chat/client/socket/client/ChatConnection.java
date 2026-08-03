package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.dto.PublishedChatMessage;

import java.io.IOException;
public interface ChatConnection extends AutoCloseable {

    void send(PublishedChatMessage message) throws IOException;

    PublishedChatMessage receive()
            throws IOException, ClassNotFoundException;

    boolean isOpen();

    @Override
    void close() throws IOException;
}