package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.repository.GroupMembershipRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import org.springframework.stereotype.Service;

@Service
public class GroupMembershipServiceImpl implements GroupMembershipService {
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupMembershipServiceImpl(GroupMembershipRepository groupMembershipRepository) {
        this.groupMembershipRepository = groupMembershipRepository;
    }
}
