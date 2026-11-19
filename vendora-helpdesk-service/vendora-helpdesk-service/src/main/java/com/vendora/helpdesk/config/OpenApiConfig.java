package com.vendora.helpdesk.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI helpdeskServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vendora Helpdesk Service API")
                        .description("Campus Facilities, Maintenance Ticketing, Staff Assignment, and Resolution Thread APIs")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("API Gateway (Port 8080)"),
                        new Server().url("http://localhost:8084").description("Direct Helpdesk Service (Port 8084)"),
                        new Server().url("/").description("Current Server (Relative)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token obtained from /api/auth/login")));
    }
}
