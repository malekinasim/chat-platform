package com.nasim.chat.client.socket.client;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.client.socket.listener.MessageListener;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ChatClientReceiver {

    private final ChatConnection chatConnection;
    private final MessageListener messageListener;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private AtomicBoolean running = new AtomicBoolean(false);

    public ChatClientReceiver(ChatConnection chatConnection, MessageListener messageListener) {
        this.chatConnection = chatConnection;
        this.messageListener = messageListener;
    }


    // api and stream mode: listens in background thread
    public void startListeningAsync() {
        if(!chatConnection.isOpen())
            System.out.println("Server closed the connection.");
        if (!running.compareAndSet(false, true)) {
            throw new IllegalStateException(
                    "Receiver is already running"
            );
        }

        executor.submit(this::runListeningLoop);

    }

    private void runListeningLoop() {
        try {
            while (running.get()) {
                ChatMessage message = chatConnection.receive();
                messageListener.dispatch(message);
            }

            System.out.println("Server closed the connection.");
        } catch (IOException e) {
            if (running.get()) {
                System.out.println("Socket error: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            if (running.get()) {
                System.out.println("object cast error: " + e.getMessage());
            }
        }
    }

}
