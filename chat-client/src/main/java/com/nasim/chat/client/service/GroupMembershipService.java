package com.nasim.chat.client.service;

import com.nasim.chat.client.model.entity.ChatGroup;
import com.nasim.chat.model.dto.ChatGroupDto;

import java.util.Arrays;
import java.util.List;

public interface GroupMembershipService {
    boolean hasActiveMembership(String userId, String roomCode);

    List<ChatGroupDto> getUserActiveGroup(String userId);

    List<String> findMemberIdsByGroupCode(String groupCode);
}
