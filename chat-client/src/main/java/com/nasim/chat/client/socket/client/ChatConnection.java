package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.ChatMessage;


import java.io.*;
public interface ChatConnection extends AutoCloseable {

    void send(ChatMessage message) throws IOException;

    ChatMessage receive()
            throws IOException, ClassNotFoundException;

    boolean isOpen();

    @Override
    void close() throws IOException;
}