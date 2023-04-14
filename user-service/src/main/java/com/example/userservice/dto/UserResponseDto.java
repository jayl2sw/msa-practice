package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseDto {
    private String email;
    private String name;
    private String userId;

    private List<OrderResponseDto> orders;
}
