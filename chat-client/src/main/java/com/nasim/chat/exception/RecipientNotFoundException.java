package com.nasim.chat.exception;
public class RecipientNotFoundException extends RuntimeException {

    public RecipientNotFoundException(String receiverId) {
        super("Recipient does not exist or is inactive: " + receiverId);
    }
}