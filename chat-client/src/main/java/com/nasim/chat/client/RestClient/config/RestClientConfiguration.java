package com.nasim.chat.client.RestClient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient userServiceClient(
            RestClient.Builder builder,
            @Value("${services.auth-service.base-url}")
            String authServiceBaseUrl
    ) {
        return builder
                .baseUrl(authServiceBaseUrl)
                .build();
    }
}
