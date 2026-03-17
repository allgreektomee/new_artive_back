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
                        // 1. OPTIONS 요청(Preflight)은 무조건 1순위
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. [변경] articles 관련 경로를 가장 명시적으로 분리
                        // 쿼리 파라미터가 붙는 경우를 대비해 기본 경로와 와일드카드를 모두 명시합니다.
                        .requestMatchers("/api/v1/articles").permitAll()
                        .requestMatchers("/api/v1/articles/**").permitAll()
                        .requestMatchers("/api/v1/artworks").permitAll()    // 리스트 호출용 (?page=0 대응)
                        .requestMatchers("/api/v1/artworks/**").permitAll() // 상세 페이지 호출용
                        // 3. 나머지 공개 API
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/images/**",
                                "/api/v1/config/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/hello"
                        ).permitAll()

                        // 4. 관리자 API (필요시)
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