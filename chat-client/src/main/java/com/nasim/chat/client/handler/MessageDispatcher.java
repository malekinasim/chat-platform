package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;

public interface MessageDispatcher {

     void dispatch(PublishedChatMessage publishedChatMessage);

}
