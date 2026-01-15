package com.artivefor.me.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server; // Server import 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI artiveOpenAPI() {
        // 1. 운영 환경 및 로컬 환경 서버 주소 설정
        Server prodServer = new Server();
        prodServer.setUrl("https://api.artivefor.me");
        prodServer.setDescription("운영 서버 (HTTPS)");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("로컬 테스트 서버");

        // 2. 보안 스키마 이름 정의
        String securitySchemeName = "bearerAuth";

        // 3. 전체 API에 보안 요구사항 적용
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        // 4. JWT 인증 방식 설정
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("Artive API")
                        .description("Artive 프로젝트 API 명세서")
                        .version("v1.0.0"))
                .servers(Arrays.asList(prodServer, localServer)) // 서버 목록 등록
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}