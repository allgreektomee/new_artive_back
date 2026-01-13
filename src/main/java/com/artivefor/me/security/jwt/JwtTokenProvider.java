package com.artivefor.me.security.jwt;

import com.artivefor.me.data.user.ArtiveUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private final UserDetailsService userDetailsService; // 우리 서비스 주입

    private Key key;

    @PostConstruct
    protected void init() {
        // secretKey를 Base64로 인코딩하여 안전하게 키 생성
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    // 1. 토큰 생성
    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role); // 토큰에 유저 권한 정보 포함

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 토큰에서 유저 이메일 추출
    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    // 4. 토큰으로부터 인증 정보 객체(Authentication) 생성
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String email = getEmail(token);

        // ⭐️ 중요: 스프링 기본 User가 아니라, DB에서 조회한 'ArtiveUser'를 가져옵니다.
        // loadUserByUsername이 ArtiveUser를 반환하도록 설정되어 있어야 합니다.
        ArtiveUser principal = (ArtiveUser) userDetailsService.loadUserByUsername(email);

        // principal 자리에 ArtiveUser 객체를 그대로 넣습니다.
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }



}