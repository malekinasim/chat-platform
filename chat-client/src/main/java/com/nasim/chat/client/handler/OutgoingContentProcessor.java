package com.nasim.chat.client.handler;

import com.nasim.chat.client.model.dto.ChatMessage;
import com.nasim.chat.client.model.dto.ContentType;

public interface OutgoingContentProcessor
        extends Handler<ContentType> {

    ChatMessage process(ChatMessage message);
}
