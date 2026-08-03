package com.nasim.chat.client.service;

import java.util.List;

public interface UserDirectoryClient {
    List<String> findAllActiveUserIds();
    Boolean isUserActive(String userID);
}
