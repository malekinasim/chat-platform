package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {

}
