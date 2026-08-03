package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.MessageContentType;

public interface OutgoingContentProcessor extends Handler<MessageContentType> {
    PublishedChatMessage process(PublishedChatMessage message);
}
