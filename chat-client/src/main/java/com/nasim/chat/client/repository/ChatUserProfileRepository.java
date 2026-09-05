package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.ChatUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChatUserProfileRepository extends JpaRepository<ChatUserProfile, String> {
    List<ChatUserProfile> findAllByUserIdIn(Collection<String> userIds);
}
