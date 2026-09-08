package com.nasim.chat.auth_service.config;

import com.nasim.chat.security.config.ResourceServerAuthorizationRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationConfiguration {

    @Bean
    ResourceServerAuthorizationRules chatAuthorizationRules() {
        return authorize -> authorize
                .requestMatchers(
                        "/auth/login",
                        "/login/**",
                        "/oauth2/**",
                        "/api/auth/token/**",
                        "/api/auth/onboarding/complete",
                        "/actuator/health"

                ).permitAll()
                .anyRequest().authenticated();

    }
}
