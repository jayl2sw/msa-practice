package com.example.userservice.entity;

import com.example.userservice.dto.SignupCommand;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.common.util.Encryptor;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 60, unique = true)
    private String encryptedPassword;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(name="token")
    private String token;

    public static UserEntity from(SignupCommand signupCommand) {
        return UserEntity.builder()
                .email(signupCommand.getEmail())
                .name(signupCommand.getName())
                .encryptedPassword(Encryptor.encryptPassword(signupCommand.getPassword()))
                .build();
    }

    public UserResponseDto toResponseDto(){
        return UserResponseDto.builder()
                .email(this.email)
                .name(this.name)
                .userId(this.userId).build();
    }

    public void saveToken(String token) {
        this.token = token;
    }
}
