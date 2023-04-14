package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.dto.OrderResponseDto;
import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.AccessDeniedException;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto){
        Order order = Order.fromRequestDto(orderRequestDto);
        orderRepository.save(order);

        return order.toResponseDto();
    };
    public OrderResponseDto getOrderByOrderId(String orderId, String userId){
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
        if (!order.getUserId().equals(userId)) {
             throw new AccessDeniedException();
        }
        return order.toResponseDto();
    }
    public List<OrderResponseDto> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream().map(order -> order.toResponseDto()).collect(Collectors.toList());
    }
}
