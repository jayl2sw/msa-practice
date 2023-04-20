package com.example.userservice.service;

import com.example.userservice.dto.*;
import com.example.userservice.entity.TokenDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.common.exceptions.user.UserNotFoundException;
import com.example.userservice.common.jwt.TokenProvider;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    public UserResponseDto createUser(SignupCommand signupCommand){
        UserEntity userEntity = UserEntity.from(signupCommand);
        userEntity.setUserId(UUID.randomUUID().toString());
        userRepository.save(userEntity);
        return userEntity.toResponseDto();
    }

    public UserResponseDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);

        UserResponseDto userResponseDto = userEntity.toResponseDto();

        List<OrderResponseDto> orderLists = new ArrayList<>();
        userResponseDto.setOrders(orderLists);

        return userResponseDto;

    }



    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll().stream().map(user -> user.toResponseDto()).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

    public UserEntity getUserDetailsByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public TokenDto doLogin(LoginCommand loginCommand) {
        // Login id/pw로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginCommand.getEmail(), loginCommand.getPassword());

        // 검증 과정
        // CustomUserDetailsService의 loadByUserName 실행
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // Refresh Token 저장
        Optional<UserEntity> entity = userRepository.findByEmail(authentication.getName());

        if(entity.isPresent()){
            entity.get().saveToken(tokenDto.getRefreshToken());
            userRepository.save(entity.get());
        }

        // 토큰 발급
        return tokenDto;
    }

    public TokenDto refresh(TokenCommand requestDto){
        // Refresh Token 검증
        if(!tokenProvider.validateToken(requestDto.getRefreshToken())){
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // Access Token에서 Id(username) 가져오기
        Authentication authentication = tokenProvider.getAuthentication(requestDto.getAccessToken());

        // 가져온 ID로 Refresh Token 가져오기
        UserEntity entity = userRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new RuntimeException("로그아웃된 사용자입니다."));

        String refreshToken = entity.getToken();

        // 일치 검사
        if(!refreshToken.equals(requestDto.getRefreshToken())){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 새 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // DB 정보 업데이트
        entity.saveToken(tokenDto.getRefreshToken());
        userRepository.save(entity);

        // 토큰 발급
        return tokenDto;
    }
}
