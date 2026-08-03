package com.nasim.chat.client.repository;

import com.nasim.chat.client.model.entity.MessageReceiver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReceiverRepository extends JpaRepository<MessageReceiver,Long> {
}
