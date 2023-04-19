package com.example.userservice.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletInputStream;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Slf4j
public class LoginCommand {
    @NotNull(message = "Email cannot be null")
    @Size(min=2, message="Email not be less than 2 characters")
    @Email
    private String email;

    @NotNull(message="Password cannot be null")
    @Size(min=8, message="Password must be equals or more than 8 characters")
    private String password;

    public static LoginCommand from(ServletInputStream inputStream) {
        return null;
    }
}
