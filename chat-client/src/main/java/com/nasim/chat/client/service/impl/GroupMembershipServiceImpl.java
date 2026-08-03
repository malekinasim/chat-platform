package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.entity.ChatGroup;
import com.nasim.chat.client.model.entity.GroupMembership;
import com.nasim.chat.client.repository.GroupMembershipRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.model.dto.ChatGroupDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GroupMembershipServiceImpl implements GroupMembershipService {
    private final GroupMembershipRepository groupMembershipRepository;

    public GroupMembershipServiceImpl(GroupMembershipRepository groupMembershipRepository) {
        this.groupMembershipRepository = groupMembershipRepository;
    }

    @Override
    public boolean hasActiveMembership(String userId, String roomCode) {
        return  groupMembershipRepository.existsByUserIdAndGroup_GroupCodeAndActiveTrue(userId,roomCode);
    }

    @Override
    public List<ChatGroupDto> getUserActiveGroup(String userId) {
        return groupMembershipRepository.findUserAllActiveGroup(userId)
                .stream().map( groupMembership -> new ChatGroupDto(groupMembership.getGroup().getGroupName(),groupMembership.getGroup().getGroupCode())
        ).toList();
    }

    @Override
    public List<String> findMemberIdsByGroupCode(String groupCode) {
       return groupMembershipRepository.findMembersByGroupCode(groupCode)
               .stream().map(GroupMembership::getUserId).toList();
    }
}
