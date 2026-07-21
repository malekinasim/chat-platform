package com.nasim.chat.client;

import com.nasim.chat.client.socket.client.ChatClientReceiver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.channels.SocketChannel;

@SpringBootTest
class MessageListenerApplicationTests {

	@MockitoBean
	private SocketChannel socketChannel;

	@MockitoBean
	private ChatClientReceiver chatClientReceiver;

	@Test
	void contextLoads() {
	}
}