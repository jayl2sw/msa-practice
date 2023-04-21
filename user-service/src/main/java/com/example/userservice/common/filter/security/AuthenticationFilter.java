package com.example.userservice.common.filter.security;

import antlr.Token;
import com.example.userservice.common.jwt.TokenProvider;
import com.example.userservice.dto.LoginCommand;
import com.example.userservice.entity.TokenDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Filter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                TokenProvider tokenProvider,
                                Environment env) {
        super(authenticationManager);
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.env = env;
    }

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final Environment env;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginCommand creds = objectMapper.readValue(request.getInputStream(), LoginCommand.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult){
        TokenDto tokenDto = tokenProvider.generateTokenDto(authResult);

        response.setHeader("auth", tokenDto.getAccessToken());
        response.setHeader("refresh", tokenDto.getRefreshToken());

    }
}
