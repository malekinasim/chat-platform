package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.repository.ChatGroupRepository;
import com.nasim.chat.client.service.ChatGroupService;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupServiceImpl implements ChatGroupService {
    private final ChatGroupRepository chatGroupRepository;

    public ChatGroupServiceImpl(ChatGroupRepository chatGroupRepository) {
        this.chatGroupRepository = chatGroupRepository;
    }
}
