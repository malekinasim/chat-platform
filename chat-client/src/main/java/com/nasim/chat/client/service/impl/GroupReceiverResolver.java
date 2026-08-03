package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.ReceiverResolver;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class GroupReceiverResolver implements ReceiverResolver {
    private final GroupMembershipService groupMembershipService;
    private final UserDirectoryClient userDirectoryClient;

    public GroupReceiverResolver(GroupMembershipService groupMembershipService, UserDirectoryClient userDirectoryClient) {
        this.groupMembershipService = groupMembershipService;
        this.userDirectoryClient = userDirectoryClient;
    }

    @Override
    public DeliveryType supportedType() {
        return DeliveryType.GROUP;
    }

    @Override
    public List<String> resolveReceiverIds(SendMessageCommand command,JwtAuthenticationToken authentication) {
        List<String> memberIds=  groupMembershipService.findMemberIdsByGroupCode(command.room()).stream()
                .filter(memberId -> !memberId.equals(command.sender()))
                .toList();
        String accessToken =
                authentication.getToken().getTokenValue();
        return userDirectoryClient.findAllValidMambers(memberIds,accessToken);
    }
}
