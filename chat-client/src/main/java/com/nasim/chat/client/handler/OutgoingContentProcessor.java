package com.nasim.chat.client.handler;

import com.nasim.chat.model.ChatMessage;
import com.nasim.chat.model.ContentType;

public interface OutgoingContentProcessor
        extends Handler<ContentType> {

    ChatMessage process(ChatMessage message);
}
