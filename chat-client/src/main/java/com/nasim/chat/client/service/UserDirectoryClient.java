package com.nasim.chat.client.service;

import com.nasim.chat.client.model.dto.DirectoryUser;

import java.util.List;

public interface UserDirectoryClient {
    List<String> findAllActiveUserIds();
    boolean userExists(String receiver);

    List<String> findAllValidMembers(List<String> memberIds);

    List<DirectoryUser> findUserDetails(List<String> userIds);
}
