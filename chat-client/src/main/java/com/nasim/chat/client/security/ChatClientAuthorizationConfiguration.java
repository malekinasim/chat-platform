package com.nasim.chat.client.security;

import com.nasim.chat.security.config.ResourceServerAuthorizationRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientAuthorizationConfiguration {

    @Bean
    ResourceServerAuthorizationRules chatAuthorizationRules() {
        return authorize -> authorize
                .requestMatchers("/health","/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/chat/**").hasRole("USER");

    }


}
