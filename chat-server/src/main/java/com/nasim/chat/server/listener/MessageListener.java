package com.nasim.chat.server.listener;


import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.server.client.ClientConnection;

public interface MessageListener {

     void dispatch(PublishedChatMessage publishedChatMessage, ClientConnection sourceClient);

}
