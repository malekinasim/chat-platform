package com.nasim.chat.server.listener;


import com.nasim.chat.model.dto.ChatMessage;

public interface MessageListener {

     void dispatch(ChatMessage chatMessage);

}
