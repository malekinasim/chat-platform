package com.nasim.chat.server.runner;

import com.nasim.chat.server.client.ClientRegistryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Component
public class ChatServerRunner implements CommandLineRunner {


    private final ServerSocketChannel serverSocketChannel;
    private final ClientRegistryService clientRegistryService;

    public ChatServerRunner(ServerSocketChannel serverSocketChannel, ClientRegistryService clientRegistryService) {
        this.serverSocketChannel = serverSocketChannel;
        this.clientRegistryService = clientRegistryService;
    }


    @Override
    public void run(String... args) {
        try {
            while (serverSocketChannel.isOpen()) {
                SocketChannel channel = serverSocketChannel.accept();
                clientRegistryService.register(channel);
            }
        } catch (IOException e) {
            System.out.println("Could not init server: " + e.getMessage());
        }
    }


}
