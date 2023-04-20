package com.example.userservice.common.jwt;


import com.example.userservice.entity.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider  implements InitializingBean {
    private final Environment env;
    private long ACCESS_TOKEN_EXPIRE_TIME;
    private long REFRESH_TOKEN_EXPIRE_TIME;
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BERAER_TYPE = "bearer";

    private String secret;
    private Key key;

    public TokenProvider(
            Environment env
    ){
        this.env = env;
        this.secret = env.getProperty("token.secret");
        this.ACCESS_TOKEN_EXPIRE_TIME = Long.parseLong(env.getProperty("token.refresh_expiration_time"));
        this.REFRESH_TOKEN_EXPIRE_TIME = Long.parseLong(env.getProperty("token.refresh_expiration_time"));
    }

    @Override
    public void afterPropertiesSet(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public TokenDto generateTokenDto(String email, String authorities){
        Date accessTokenExpiresIn = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME);
        log.info(accessTokenExpiresIn.toString());
        // Access Token
        String accessToken = Jwts.builder()
                .setSubject(email) // sub : name(email)
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
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto generateTokenDto(Authentication authentication){
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return generateTokenDto(authentication.getName(), authorities);
    }

    public Authentication getAuthentication(String token){
        // 토큰 복호화
        Claims claims = parseClaims(token);

        if(claims.get(AUTHORITIES_KEY)==null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져옴
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체 생성, Authenticaition 리턴
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            e.printStackTrace();
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String token){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
