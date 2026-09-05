package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.DirectoryUser;

import java.util.List;

public interface UserDirectoryClient {
    List<String> findAllActiveUserIds(String accessToken);
    boolean userExists(String receiver,String accessToken);

    List<String> findAllValidMembers(List<String> memberIds, String accessToken);

    List<DirectoryUser> findUserDetails(List<String> userIds);
}
