package com.example.orderservice.exception;

public class AccessDeniedException extends RuntimeException{
    private String message;

    public AccessDeniedException() {
        this.message = "해당 주문에 접근할 수 없습니다.";
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
