package com.nasim.chat.client.socket.listener;

import com.nasim.chat.model.dto.PublishedChatMessage;

public interface MessageListener {

     void dispatch(PublishedChatMessage publishedChatMessage);

}
