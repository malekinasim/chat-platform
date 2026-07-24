package com.nasim.chat.auth_service.exceptions;

public class InvalidExchangeCodeException extends RuntimeException {
    public InvalidExchangeCodeException() {
        super("Invalid exchange code");
    }
}