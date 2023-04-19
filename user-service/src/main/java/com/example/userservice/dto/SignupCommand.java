package com.example.userservice.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignupCommand {
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message="Email not be less then 2 characters")
    @Email
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message="Email not be less then 2 characters")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message="Password must be equal or greater then 8 characters")
    private String password;
}