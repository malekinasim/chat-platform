package com.nasim.chat.client.socket.listener;

import com.nasim.chat.model.dto.ChatMessage;

public interface MessageListener {

     void dispatch(ChatMessage chatMessage);

}
