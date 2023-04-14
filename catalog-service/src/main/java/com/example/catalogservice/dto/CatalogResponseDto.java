package com.example.catalogservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogResponseDto {
    private String productId;
    private Integer quantity;
    private Integer unitPrice;
    private Integer stock;
    private LocalDateTime createdAt;

}
