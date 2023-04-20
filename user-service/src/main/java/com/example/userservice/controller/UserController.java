package com.example.userservice.controller;

import com.example.userservice.common.exceptions.common.InvalidParameterException;
import com.example.userservice.dto.LoginCommand;
import com.example.userservice.dto.SignupCommand;
import com.example.userservice.dto.TokenCommand;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.TokenDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody SignupCommand signupCommand) {
        UserResponseDto user = userService.createUser(signupCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<TokenDto> doLogin(@Valid @RequestBody LoginCommand loginCommand, BindingResult result){
        if(result.hasErrors()){
            throw new InvalidParameterException(result);
        }
        TokenDto tokenDto = userService.doLogin(loginCommand);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Auth", tokenDto.getAccessToken());
        headers.add("Refresh", tokenDto.getRefreshToken());

        return new ResponseEntity<>(tokenDto, headers, HttpStatus.OK);
    }


    /**
     * Refresh Token 재발급
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenDto> refresh(@RequestBody TokenCommand requestDto){
        return new ResponseEntity<>(userService.refresh(requestDto), HttpStatus.OK);
    }


    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByUserId(userId));
    }


}
