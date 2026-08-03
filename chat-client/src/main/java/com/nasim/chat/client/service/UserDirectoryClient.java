package com.nasim.chat.client.service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public interface UserDirectoryClient {
    List<String> findAllActiveUserIds(String accessToken);
    boolean userExists(String receiver,String accessToken);

    List<String> findAllValidMambers(List<String> memberIds, String accessToken);
}
