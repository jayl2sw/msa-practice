package com.example.userservice.service;

import com.example.userservice.dto.OrderResponseDto;
import com.example.userservice.dto.SignupCommand;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.exceptions.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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
}
