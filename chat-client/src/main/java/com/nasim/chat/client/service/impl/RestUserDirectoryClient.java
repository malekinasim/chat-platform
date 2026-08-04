package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.UserDirectoryClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RestUserDirectoryClient implements UserDirectoryClient {

    private final RestClient userServiceClient;

    public RestUserDirectoryClient (@Qualifier("userServiceClient") RestClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public List<String> findAllActiveUserIds(String accessToken) {
        return userServiceClient.get()
                .uri("/internal/users/active/ids")
                .headers(headers ->
                        headers.setBearerAuth(accessToken)
                )
                .retrieve()
                .body( new ParameterizedTypeReference<List<String>>() {});
    }

    @Override
    public boolean userExists(String receiver, String accessToken) {
        Boolean response = userServiceClient.get()
                .uri("/internal/users/{receiver}/exists", receiver)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(Boolean.class);

        return Boolean.TRUE.equals(response);
    }

    @Override
    public List<String> findAllValidMembers(List<String> memberIds, String accessToken) {
        return userServiceClient.post()
                .uri("/internal/users/active/ids/filter")
                .body(memberIds)
                .headers(headers ->
                        headers.setBearerAuth(accessToken)
                )
                .retrieve()
                .body( new ParameterizedTypeReference<List<String>>() {});
    }
}