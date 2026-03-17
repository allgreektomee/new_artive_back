package com.artivefor.me.security.config;

import com.artivefor.me.security.jwt.JwtAuthenticationFilter;
import com.artivefor.me.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1순위: OPTIONS(CORS)는 무조건 통과
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // 2순위: API 문서 및 스웨거 (외부에서 명세를 봐야 하므로 필수!)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 3순위: [핵심] 작품 및 아티클 조회 GET API 완전 개방
                        // URL 뒤에 쿼리 스트링(?page=0)이 붙어도 통과되도록 /** 를 붙입니다.
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/artworks/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/artworks").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/articles/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/articles").permitAll()

                        // 4순위: 기타 이미지 및 설정 정보
                        .requestMatchers("/api/v1/images/**", "/api/v1/config/**", "/api/v1/auth/**").permitAll()

                        // 5순위: 관리자용 수정/등록/삭제(POST, PUT, DELETE)는 인증 필수!
                        // 이 구문이 있어야 '목록 볼 때 PUT이 날아가는 상황'에서 시큐리티가 403으로 막아줍니다.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 💡 패턴 기반으로 도메인 허용 (더 확실함)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://www.artivefor.me",
                "https://artivefor.me",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:5175"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 💡 모든 헤더 허용으로 단순화하여 충돌 방지
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 브라우저가 CORS 정보를 1시간 동안 기억하도록 설정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}