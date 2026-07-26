package com.nasim.chat.auth_service.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private String code;
    public CustomException(String message,String code) {
        super(message); this.code=code;
    }
}