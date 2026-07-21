package com.nasim.chat.client;

import com.nasim.chat.client.socket.client.ChatClientReceiver;
import com.nasim.chat.client.socket.client.ChatConnection;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.channels.SocketChannel;

@SpringBootTest
class MessageListenerApplicationTests {

	@MockitoBean
	private ChatConnection chatConnection;

	@MockitoBean
	private ChatClientReceiver chatClientReceiver;

	@Test
	void contextLoads() {
	}
}