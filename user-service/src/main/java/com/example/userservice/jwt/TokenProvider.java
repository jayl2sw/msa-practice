package com.example.userservice.jwt;


import com.example.userservice.entity.TokenDto;
import com.example.userservice.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BERAER_TYPE = "bearer";
    @Value("${token.access_expiration_time}")
    private static long ACCESS_TOKEN_EXPIRE_TIME;
    @Value("${token.refresh_expiration_time}")
    private static long REFRESH_TOKEN_EXPIRE_TIME;

    private String secret;
    private Key key;

    public TokenProvider(
            @Value("${token.secret}") String secret
    ){
        this.secret = secret;
    }

    public TokenDto generateTokenDto(UserEntity user, String authorities){
        Date accessTokenExpiresIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME);

        // Access Token
        String accessToken = Jwts.builder()
                .setSubject(user.getEmail()) // sub : name(email)
                .claim(AUTHORITIES_KEY, authorities) // auth: ROLE
                .setExpiration(accessTokenExpiresIn) // exp: ~~~
                .signWith(key, SignatureAlgorithm.HS512) // alg: HS512
                .compact();

        // Refresh Token
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BERAER_TYPE)
                .accessToken(accessToken)
                .userId(user.getUserId())
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }
}
