package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.dto.DirectoryUser;
import com.nasim.chat.client.model.dto.RoomPresenceResponse;
import com.nasim.chat.client.model.entity.ChatUserProfile;
import com.nasim.chat.client.repository.ChatUserProfileRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.client.service.UserDirectoryClient;
import com.nasim.chat.client.storages.RoomPresenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomPresenceServiceImplTest {
    @Mock RoomPresenceStore roomPresenceStore;
    @Mock UserDirectoryClient userDirectoryClient;
    @Mock ChatUserProfileRepository chatUserProfileRepository;
    @Mock GroupMembershipService groupMembershipService;

    private RoomPresenceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RoomPresenceServiceImpl(
                roomPresenceStore, userDirectoryClient, chatUserProfileRepository, groupMembershipService);
    }

    @Test
    void returnsEmptyPresenceWithoutCallingDependenciesForProfiles() {
        when(groupMembershipService.hasActiveMembership("requester", "room")).thenReturn(true);
        when(roomPresenceStore.onlineUsers("room")).thenReturn(Set.of());

        RoomPresenceResponse response = service.getRoomPresence("room", "requester", "token");

        assertThat(response.roomCode()).isEqualTo("room");
        assertThat(response.onlineUsers()).isEmpty();
        assertThat(response.onlineUserCount()).isZero();
        verify(userDirectoryClient, never()).findUserDetails(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.anyString());
        verify(chatUserProfileRepository, never()).findAllByUserIdIn(org.mockito.ArgumentMatchers.anyCollection());
    }

    @Test
    void composesUsersInIdOrderUsingOneBatchCallAndOneBatchQuery() {
        when(groupMembershipService.hasActiveMembership("requester", "room")).thenReturn(true);
        when(roomPresenceStore.onlineUsers("room")).thenReturn(Set.of("2", "1", "3"));
        when(userDirectoryClient.findUserDetails(List.of("1", "2", "3"), "token"))
                .thenReturn(List.of(
                        new DirectoryUser("2", "second", "auth-2"),
                        new DirectoryUser("1", "first", "auth-1")
                ));
        ChatUserProfile replacement = profile("2", "chat-2");
        ChatUserProfile nullAvatar = profile("1", null);
        when(chatUserProfileRepository.findAllByUserIdIn(List.of("1", "2", "3")))
                .thenReturn(List.of(replacement, nullAvatar));

        RoomPresenceResponse response = service.getRoomPresence("room", "requester", "token");

        assertThat(response.onlineUsers())
                .extracting("userId", "username", "avatarUrl")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("1", "first", "auth-1"),
                        org.assertj.core.groups.Tuple.tuple("2", "second", "chat-2")
                );
        assertThat(response.onlineUserCount()).isEqualTo(2);
        verify(userDirectoryClient).findUserDetails(List.of("1", "2", "3"), "token");
        verify(chatUserProfileRepository).findAllByUserIdIn(List.of("1", "2", "3"));
    }

    @Test
    void toleratesNullDirectoryResponse() {
        when(groupMembershipService.hasActiveMembership("requester", "room")).thenReturn(true);
        when(roomPresenceStore.onlineUsers("room")).thenReturn(Set.of("1"));
        when(userDirectoryClient.findUserDetails(List.of("1"), "token")).thenReturn(null);
        when(chatUserProfileRepository.findAllByUserIdIn(List.of("1"))).thenReturn(List.of());

        assertThat(service.getRoomPresence("room", "requester", "token").onlineUsers()).isEmpty();
    }

    @Test
    void rejectsRequesterWhoIsNotAnActiveRoomMemberBeforeReadingPresence() {
        when(groupMembershipService.hasActiveMembership("requester", "room")).thenReturn(false);

        assertThatThrownBy(() -> service.getRoomPresence("room", "requester", "token"))
                .isInstanceOf(AuthorizationDeniedException.class);
        verify(roomPresenceStore, never()).onlineUsers("room");
    }

    private static ChatUserProfile profile(String userId, String avatarUrl) {
        ChatUserProfile profile = new ChatUserProfile();
        profile.setUserId(userId);
        profile.setAvatarUrl(avatarUrl);
        return profile;
    }
}
