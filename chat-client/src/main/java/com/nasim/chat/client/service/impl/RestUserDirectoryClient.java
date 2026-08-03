package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.UserDirectoryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RestUserDirectoryClient implements UserDirectoryClient {

    private final RestClient userServiceClient;

    public RestUserDirectoryClient(RestClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public List<String> findAllActiveUserIds() {
        return userServiceClient.get()
                .uri("/internal/users/active/ids")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Boolean isUserActive(String userID) {
        return true;
    }
}