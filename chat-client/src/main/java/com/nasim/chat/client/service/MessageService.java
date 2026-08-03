package com.nasim.chat.client.service;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.model.entity.ReceiverStatus;
import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;

import java.util.List;

public interface MessageService {
    Message saveTextMessage(SendMessageCommand sendMessageCommand, List<String> receiver);

}
