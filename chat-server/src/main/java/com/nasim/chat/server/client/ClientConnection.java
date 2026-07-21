package com.nasim.chat.server.client;

import lombok.Getter;
import com.nasim.chat.model.ChatMessage;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.UUID;


public class ClientConnection implements Closeable {

    private final SocketChannel socketChannel;

    @Getter
    private final InetSocketAddress inetSocketAddress;

    @Getter
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

    public synchronized void send(ChatMessage message)
            throws IOException {

        output.writeObject(message);
        output.flush();
        output.reset();
    }

    public ChatMessage receive()
            throws IOException, ClassNotFoundException {

        Object value = input.readObject();

        if (!(value instanceof ChatMessage message)) {
            throw new StreamCorruptedException(
                    "Expected ChatMessage but received: "
                            + value.getClass().getName()
            );
        }

        return message;
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

