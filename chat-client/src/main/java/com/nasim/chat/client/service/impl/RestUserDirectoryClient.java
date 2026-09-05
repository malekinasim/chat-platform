package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.DirectoryUser;
import com.nasim.chat.client.security.SecurityUtils;
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
    public List<String> findAllActiveUserIds() {
        List<String> response= userServiceClient.get()
                .uri("/internal/users/active/ids")
                .headers(headers ->
                        headers.setBearerAuth(SecurityUtils.authenticatedAccessToken())
                )
                .retrieve()
                .body( new ParameterizedTypeReference<List<String>>() {});


        return response == null ? List.of() : response;
    }

    @Override
    public boolean userExists(String receiver) {
        Boolean response = userServiceClient.get()
                .uri("/internal/users/{receiver}/exists", receiver)
                .headers(headers -> headers.setBearerAuth(SecurityUtils.authenticatedAccessToken()))
                .retrieve()
                .body(Boolean.class);

        return Boolean.TRUE.equals(response);
    }

    @Override
    public List<String> findAllValidMembers(List<String> memberIds) {
        List<String> response = userServiceClient.post()
                .uri("/internal/users/active/ids/filter")
                .body(memberIds)
                .headers(headers ->
                        headers.setBearerAuth(SecurityUtils.authenticatedAccessToken())
                )
                .retrieve()
                .body( new ParameterizedTypeReference<List<String>>() {});
        return response == null ? List.of() : response;
    }

    @Override
    public List<DirectoryUser> findUserDetails(List<String> userIds) {
        List<DirectoryUser> response = userServiceClient.post()
                .uri("/internal/users/details")
                .body(userIds)
                .headers(headers -> headers.setBearerAuth(SecurityUtils.authenticatedAccessToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<List<DirectoryUser>>() {});
        return response == null ? List.of() : response;
    }
}
