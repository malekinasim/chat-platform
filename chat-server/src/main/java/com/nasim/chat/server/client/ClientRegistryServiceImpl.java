package com.nasim.chat.server.client;


import com.nasim.chat.server.handler.ClientHandler;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ClientRegistryServiceImpl
        implements ClientRegistryService {



    private final Map<String, ClientConnection> clients =
            new ConcurrentHashMap<>();

    private final ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor();

    private final ClientHandler clientHandler;

    public ClientRegistryServiceImpl(
            ClientHandler clientHandler
    ) {
        this.clientHandler = clientHandler;
    }

    @Override
    public List<ClientConnection> getClients() {
        return List.copyOf(clients.values());
    }



    @Override
    public void register(SocketChannel socketChannel) {
        executor.submit(() -> {
            ClientConnection client = null;

            try {
                client = new ClientConnection(socketChannel);
                clients.put(client.getId(),client);
                clientHandler.handle(client);
            } catch (IOException e) {
                System.out.println(
                        "Could not initialize client: "
                                + e.getMessage()
                );
            } finally {
                if (client != null) {
                    unregister(client);
                } else {
                    closeQuietly(socketChannel);
                }
            }
        });
    }

    @Override
    public void unregister(ClientConnection client) {
        clients.remove(client.getId(),client);

        try {
            client.close();
        } catch (IOException e) {
            System.out.println(
                    "Could not close client: "
                            + e.getMessage()
            );
        }
    }

    private void closeQuietly(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException ignored) {
        }
    }

    @PreDestroy
    void shutdownServer() {
        for (ClientConnection client : clients.values()) {
            unregister(client);
        }

        executor.shutdownNow();
    }
}