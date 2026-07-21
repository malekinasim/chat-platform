package com.nasim.chat.client.socket;

import com.nasim.chat.client.socket.client.ChatConnection;
import com.nasim.chat.client.socket.client.SocketChatConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Configuration
public class SocketConfiguration {

    @Bean(destroyMethod = "close")
    public ChatConnection chatConnection(
            @Value("${chat.server.host}") String host,
            @Value("${chat.server.port}") int port
    ) throws IOException {

        SocketChannel channel = SocketChannel.open(
                new InetSocketAddress(host, port)
        );

        return new SocketChatConnection(channel);
    }
}
