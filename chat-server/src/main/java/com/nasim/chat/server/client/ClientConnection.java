package com.nasim.chat.server.client;


import com.nasim.chat.model.dto.PublishedChatMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.UUID;


public class ClientConnection implements Closeable {

    private final SocketChannel socketChannel;

    private final InetSocketAddress inetSocketAddress;

    private final String id;

    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public ClientConnection(SocketChannel socketChannel)
            throws IOException {

        this.socketChannel =
                Objects.requireNonNull(socketChannel);

        this.inetSocketAddress=(InetSocketAddress) socketChannel.getRemoteAddress();


        this.id = UUID.randomUUID().toString();
        this.output = new ObjectOutputStream(
                Channels.newOutputStream(socketChannel)
        );
        this.output.flush();

        this.input = new ObjectInputStream(
                Channels.newInputStream(socketChannel)
        );
    }

    public synchronized void send(PublishedChatMessage message)
            throws IOException {

        output.writeObject(message);
        output.flush();
        output.reset();
    }

    public PublishedChatMessage receive()
            throws IOException, ClassNotFoundException {

        Object value = input.readObject();

        if (!(value instanceof PublishedChatMessage message)) {
            throw new StreamCorruptedException(
                    "Expected ChatMessage but received: "
                            + value.getClass().getName()
            );
        }

        return message;
    }

    public String getId() {
        return id;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socketChannel.close();
    }
    public boolean isOpen() {
        return socketChannel.isOpen();
    }

}

