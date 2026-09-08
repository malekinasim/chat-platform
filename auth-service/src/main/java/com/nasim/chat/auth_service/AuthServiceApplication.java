package com.nasim.chat.auth_service;

import com.nasim.chat.security.config.JwtResourceServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@Import({JwtResourceServerConfiguration.class,})
@SpringBootApplication(scanBasePackages = {"com.nasim.chat.security.config", "com.nasim.chat.security.jwt"})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
