package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.MessageContentType;

public interface IncomingContentHandler extends Handler<MessageContentType>{
    MessageContentType supportedType();
    ChatMessage handle(ChatMessage message);
}
