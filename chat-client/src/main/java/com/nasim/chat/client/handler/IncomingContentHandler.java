package com.nasim.chat.client.handler;

import com.nasim.chat.client.model.dto.ChatMessage;
import com.nasim.chat.client.model.dto.ContentType;

public interface IncomingContentHandler extends Handler<ContentType>{
    ContentType supportedType();
    ChatMessage handle(ChatMessage message);
}
