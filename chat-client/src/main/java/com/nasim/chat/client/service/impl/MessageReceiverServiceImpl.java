package com.nasim.chat.client.service.impl;

import com.nasim.chat.client.model.entity.Message;
import com.nasim.chat.client.model.entity.MessageReceiver;
import com.nasim.chat.client.repository.MessageReceiverRepository;
import com.nasim.chat.client.service.GroupMembershipService;
import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.SendMessageCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageReceiverServiceImpl implements com.nasim.chat.client.service.MessageReceiverService {


    private final MessageReceiverRepository receiverRepository;

    public MessageReceiverServiceImpl(MessageReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }


    @Override
    public void saveReceivers(Message message, List<String> receivers) {
        List<MessageReceiver> receiverList = receivers.stream()
                        .map(memberId -> this.createReceiver(message, memberId))
                        .toList();
        receiverRepository.saveAll(receiverList);
    }
}