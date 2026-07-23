package com.nasim.chat.client;

import com.nasim.chat.security.config.JwtResourceServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({JwtResourceServerConfiguration.class,})
public class ChatClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatClientApplication.class, args);
	}

}
