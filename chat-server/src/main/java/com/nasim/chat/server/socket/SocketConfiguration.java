package com.nasim.chat.server.socket;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

@Configuration
public class SocketConfiguration {

    @Bean(destroyMethod = "close")
    public  ServerSocketChannel serverSocketChannel(
            @Value("${chat.server.port}") int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
        System.out.println("Server is listening on port " + port);

        return serverSocketChannel;
    }

}
