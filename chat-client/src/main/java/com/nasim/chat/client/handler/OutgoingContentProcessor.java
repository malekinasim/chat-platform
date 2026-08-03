package com.nasim.chat.client.handler;

import com.nasim.chat.model.dto.ChatMessage;
import com.nasim.chat.model.dto.MessageContentType;

public interface OutgoingContentProcessor
        extends Handler<MessageContentType> {

    ChatMessage process(ChatMessage message);
}
