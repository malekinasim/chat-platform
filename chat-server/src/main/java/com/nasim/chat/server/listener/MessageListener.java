package com.nasim.chat.server.listener;


import com.nasim.chat.model.dto.PublishedChatMessage;

public interface MessageListener {

     void dispatch(PublishedChatMessage publishedChatMessage);

}
