package com.example.userservice.entity;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String userId;
    private long accessTokenExpiresIn;
}