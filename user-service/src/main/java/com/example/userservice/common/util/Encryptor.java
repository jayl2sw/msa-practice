package com.example.userservice.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Encryptor {
    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
