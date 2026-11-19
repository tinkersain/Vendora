package com.vendora.gateway.filter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.vendora.gateway.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        // 1. Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        // 2. Allow Public endpoints
        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        // 3. Read Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Check Bearer token prefix
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header", path);
        }

        String token = authHeader.substring(7);

        try {
            // Validate JWT signature and expiration
            Claims claims = jwtService.validateToken(token);

            Long userId = jwtService.extractUserId(claims);
            String email = jwtService.extractEmail(claims);
            String role = jwtService.extractRole(claims);

            if (email == null || role == null) {
                return unauthorized(exchange, "Invalid token claims", path);
            }

            // Strip any client-supplied identity headers and inject trusted values from claims
            ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Email");
                        headers.remove("X-User-Role");

                        headers.set("X-User-Id", String.valueOf(userId));
                        headers.set("X-User-Email", email);
                        headers.set("X-User-Role", role);
                    })
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

            return chain.filter(modifiedExchange);

        } catch (JwtException | IllegalArgumentException ex) {
            return unauthorized(exchange, "Invalid or expired JWT token: " + ex.getMessage(), path);
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        if ("POST".equalsIgnoreCase(method)
                && ("/api/auth/login".equals(path) || "/api/registration/register".equals(path))) {
            return true;
        }
        return path.startsWith("/actuator")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.endsWith("/swagger-ui.html")
                || path.equals("/favicon.ico");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message, String path) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                timestamp, message, path);

        byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        org.springframework.core.io.buffer.DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
