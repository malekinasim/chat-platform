package com.nasim.chat.auth_service.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidExchangeCodeException.class)
    public ResponseEntity<ApiError> handleInvalidExchangeCode(
            InvalidExchangeCodeException exception
    ) {
        ApiError error = new ApiError(
                "INVALID_EXCHANGE_CODE",
                "The exchange code is invalid, expired, or has already been used."
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }
}
