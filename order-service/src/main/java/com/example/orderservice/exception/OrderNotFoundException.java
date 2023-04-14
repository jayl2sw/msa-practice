package com.example.orderservice.exception;

public class OrderNotFoundException extends RuntimeException{

    private String message;

    public OrderNotFoundException() {
        this.message = "해당 Order를 찾을 수 없습니다.";
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
