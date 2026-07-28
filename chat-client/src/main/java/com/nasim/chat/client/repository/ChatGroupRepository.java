package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatGroupRepository extends JpaRepository<ChatGroup,Long> {
}
