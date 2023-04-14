package com.example.userservice.entity;

import com.example.userservice.dto.SignupDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.util.Encryptor;
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

    public static UserEntity from(SignupDto signupDto) {
        return UserEntity.builder()
                .email(signupDto.getEmail())
                .name(signupDto.getEmail())
                .encryptedPassword(Encryptor.encryptPassword(signupDto.getPassword()))
                .build();
    }

    public UserResponseDto toResponseDto(){
        return UserResponseDto.builder()
                .email(this.email)
                .name(this.name)
                .userId(this.userId).build();
    }
}
