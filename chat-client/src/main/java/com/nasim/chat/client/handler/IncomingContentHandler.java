package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.PublishedChatMessage;
import com.nasim.chat.model.dto.MessageContentType;

public interface IncomingContentHandler extends Handler<MessageContentType>{
    MessageContentType supportedType();
    PublishedChatMessage handle(PublishedChatMessage message);
}
