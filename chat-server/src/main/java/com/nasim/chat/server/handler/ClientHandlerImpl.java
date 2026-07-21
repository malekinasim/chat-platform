package com.nasim.chat.server.handler;

import com.nasim.chat.server.listener.MessageListener;
import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.server.client.ClientConnection;
import org.springframework.stereotype.Service;
import java.io.EOFException;
import java.io.IOException;

@Service
public class ClientHandlerImpl implements ClientHandler{

    private final MessageListener messageListener;

    public ClientHandlerImpl(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void handle(ClientConnection client) {
        try {
            while (client.isOpen()) {
                ChatMessage message = client.receive();
                messageListener.dispatch( message);
            }
        } catch (EOFException e) {
            System.out.println(
                    "Client disconnected: " + client.getInetSocketAddress()
            );
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "Client connection failed: " + e.getMessage()
            );
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println(
                        "Could not close client: " + e.getMessage()
                );
            }
        }
    }
}
