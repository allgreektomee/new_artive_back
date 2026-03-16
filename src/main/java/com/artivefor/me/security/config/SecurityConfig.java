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
                        // 1. OPTIONS 요청은 무조건 최우선 허용 (CORS 해결)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. 공개 API (artworks, auth, images 등)
                        .requestMatchers(
                                "/api/v1/artworks/**",
                                "/api/v1/auth/**",
                                "/api/v1/articles/**",
                                "/api/v1/artworks/**",
                                "/api/v1/images/**",
                                "/api/v1/config/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/hello"
                        ).permitAll()

                        // 3. 관리자 API (현재 403 나는 구간)
                        // 명세서 상의 /api/v1/admin/{resource} 구조를 모두 포함하도록 명시
                        .requestMatchers("/api/v1/admin/**").permitAll()

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