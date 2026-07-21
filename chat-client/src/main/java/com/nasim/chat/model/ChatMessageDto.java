package com.nasim.chat.model;

import java.util.Objects;

public record ChatMessageDto(
        String text
) {
    public ChatMessageDto{
        Objects.requireNonNull(text, "message text not be null");
    }
}
