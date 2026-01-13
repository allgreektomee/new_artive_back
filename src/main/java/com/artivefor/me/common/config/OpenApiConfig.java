package com.artivefor.me.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI artiveOpenAPI() {
        // 1. 보안 스키마 이름 정의
        String securitySchemeName = "bearerAuth";

        // 2. 전체 API에 보안 요구사항 적용
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        // 3. JWT 인증 방식 설정
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
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}