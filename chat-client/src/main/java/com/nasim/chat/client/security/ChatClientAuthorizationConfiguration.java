package com.nasim.chat.client.security;

import com.nasim.chat.security.config.ResourceServerAuthorizationRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class ChatClientAuthorizationConfiguration {

    @Bean
    ResourceServerAuthorizationRules chatAuthorizationRules() {
        return authorize -> authorize
                .requestMatchers(
                        "/health",
                        "/login",
                        "/ws/chat",
                        "/onboarding/**",
                        "/auth/callback",
                        "/css/**",
                        "/js/**"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/chat/**").hasRole("USER");

    }
}
