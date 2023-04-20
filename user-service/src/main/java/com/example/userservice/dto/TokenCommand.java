package com.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenCommand {
    private String accessToken;
    private String refreshToken;
}
