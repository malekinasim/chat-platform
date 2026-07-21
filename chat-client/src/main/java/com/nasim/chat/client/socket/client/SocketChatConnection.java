package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.ChatMessage;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Objects;


public class SocketChatConnection implements ChatConnection {

    private final SocketChannel socketChannel;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public SocketChatConnection(SocketChannel socketChannel)
            throws IOException {

        this.socketChannel =
                Objects.requireNonNull(socketChannel);

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


    public void close() throws IOException {
        input.close();
        output.close();
        socketChannel.close();
    }
    public boolean isOpen() {
        return socketChannel.isOpen();
    }
}
