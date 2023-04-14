package com.example.orderservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequestDto {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;

    private String userId;
}
