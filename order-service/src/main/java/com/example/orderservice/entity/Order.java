package com.example.orderservice.entity;

import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.dto.OrderResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Integer unitPrice;
    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false, unique = true)
    private String orderId;
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public static Order fromRequestDto(OrderRequestDto orderRequestDto) {
        return Order.builder()
                .orderId(UUID.randomUUID().toString())
                .productId(orderRequestDto.getProductId())
                .unitPrice(orderRequestDto.getUnitPrice())
                .quantity(orderRequestDto.getQuantity())
                .totalPrice(orderRequestDto.getQuantity() * orderRequestDto.getUnitPrice())
                .userId(orderRequestDto.getUserId())
                .build();

    }

    public OrderResponseDto toResponseDto() {
        return new OrderResponseDto(this.productId, this.quantity, this.unitPrice,
                this.totalPrice, this.userId, this.orderId, this. createdAt);
    }
}
