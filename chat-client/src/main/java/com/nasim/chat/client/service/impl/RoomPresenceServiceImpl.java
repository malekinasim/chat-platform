package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.DirectoryUser;
import com.nasim.chat.client.model.dto.OnlineUser;
import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.model.entity.ChatUserProfile;
import com.nasim.chat.client.repository.ChatUserProfileRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.RoomPresenceService;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.client.storages.RoomPresenceStore;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoomPresenceServiceImpl implements RoomPresenceService {
    private final RoomPresenceStore roomPresenceStore;
    private final UserDirectoryClient userDirectoryClient;
    private final ChatUserProfileRepository chatUserProfileRepository;
    private final GroupMembershipService groupMembershipService;

    public RoomPresenceServiceImpl(
            RoomPresenceStore roomPresenceStore,
            UserDirectoryClient userDirectoryClient,
            ChatUserProfileRepository chatUserProfileRepository,
            GroupMembershipService groupMembershipService
    ) {
        this.roomPresenceStore = roomPresenceStore;
        this.userDirectoryClient = userDirectoryClient;
        this.chatUserProfileRepository = chatUserProfileRepository;
        this.groupMembershipService = groupMembershipService;
    }

    @Override
    public RoomPresenceResponse getRoomPresence(String roomCode, String requesterId) {
        if (!groupMembershipService.hasActiveMembership(requesterId, roomCode)) {
            throw new AuthorizationDeniedException("you don't have valid access right for this group");
        }

        List<String> userIds = roomPresenceStore.onlineUsers(roomCode).stream()
                .distinct()
                .sorted()
                .toList();
        if (userIds.isEmpty()) {
            return new RoomPresenceResponse(roomCode, List.of());
        }

        Map<String, DirectoryUser> directoryUsers = safeDirectoryUsers(userIds).stream()
                .filter(user -> user != null && user.userId() != null)
                .collect(Collectors.toMap(DirectoryUser::userId, Function.identity(), (first, ignored) -> first));
        Map<String, String> localAvatars = chatUserProfileRepository.findAllByUserIdIn(userIds).stream()
                .filter(profile -> profile.getAvatarUrl() != null)
                .collect(Collectors.toMap(ChatUserProfile::getUserId, ChatUserProfile::getAvatarUrl,
                        (first, ignored) -> first));

        List<OnlineUser> users = userIds.stream()
                .map(directoryUsers::get)
                .filter(user -> user != null)
                .map(user -> new OnlineUser(
                        user.userId(),
                        user.username(),
                        localAvatars.getOrDefault(user.userId(), user.avatarUrl())
                ))
                .toList();
        return new RoomPresenceResponse(roomCode, users);
    }

    private List<DirectoryUser> safeDirectoryUsers(List<String> userIds) {
        List<DirectoryUser> users = userDirectoryClient.findUserDetails(userIds);
        return users == null ? List.of() : users;
    }
}
