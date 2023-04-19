package com.example.userservice.controller;

import com.example.userservice.dto.SignupCommand;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final Greeting greeting;
    private final UserService userService;

    private final Environment env;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody SignupCommand signupCommand) {
        UserResponseDto user = userService.createUser(signupCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByUserId(userId));
    }
}
