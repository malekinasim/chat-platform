package com.nasim.chat.auth_service.config;

import com.nasim.chat.security.config.ResourceServerSecurityMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AuthResourceServerConfiguration {

    @Bean
    ResourceServerSecurityMatcher
    authResourceServerSecurityMatcher() {
        return http ->
                http.securityMatcher("/internal/**");
    }
}