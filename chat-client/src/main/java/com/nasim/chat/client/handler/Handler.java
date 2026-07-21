package com.nasim.chat.client.handler;

public interface Handler<T extends Enum<T>> {
    T supportedType();
}
