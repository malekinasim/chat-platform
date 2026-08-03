package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.repository.MessageRepository;
import com.nasim.chat.client.service.MessageReceiverService;
import com.nasim.chat.client.service.MessageService;
import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageReceiverService messageReceiverService;
    public MessageServiceImpl(MessageRepository messageRepository, MessageReceiverService messageReceiverService) {
        this.messageRepository = messageRepository;
        this.messageReceiverService = messageReceiverService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message saveTextMessage(SendMessageCommand sendMessageCommand, List<String> receiver) {
        Message message = new Message();
        message.setSenderId(sendMessageCommand.sender());
        message.setTextContent(sendMessageCommand.text());
        message.setMessageContentType(sendMessageCommand.messageContentType());
        message.setDeliveryType(sendMessageCommand.deliveryType());
        Message savedMessage = messageRepository.save(message);
        messageReceiverService.saveReceivers(savedMessage,receiver);
        return savedMessage;
    }
}
