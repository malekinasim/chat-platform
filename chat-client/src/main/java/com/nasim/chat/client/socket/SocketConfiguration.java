package com.nasim.chat.client.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Configuration
public class SocketConfiguration {

    @Bean(destroyMethod = "close")
    public SocketChannel socketChannel(
            @Value("${chat.server.host}") String host,
            @Value("${chat.server.port}") int port
    ) throws IOException {

        return SocketChannel.open(
                new InetSocketAddress(host, port)
        );
    }
}
