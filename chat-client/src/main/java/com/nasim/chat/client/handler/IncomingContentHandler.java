package com.nasim.chat.client.handler;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.ContentType;

public interface IncomingContentHandler extends Handler<ContentType>{
    ContentType supportedType();
    ChatMessage handle(ChatMessage message);
}
