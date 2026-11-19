package com.vendora.gateway.filter;

import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.vendora.gateway.service.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JwtAuthenticationFilterTest {

    private final String secret = "VmVuZG9yYUxvY2FsSnd0U2VjcmV0S2V5MzJCeXRlcyE=";
    private JwtService jwtService;
    private JwtAuthenticationFilter filter;
    private GatewayFilterChain chain;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret);
        filter = new JwtAuthenticationFilter(jwtService);
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("filter - Allow OPTIONS preflight request")
    void testPreflightOptions() {
        MockServerHttpRequest request = MockServerHttpRequest.method(HttpMethod.OPTIONS, "/api/listings").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("filter - Allow Public login endpoint")
    void testPublicLoginEndpoint() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("filter - Allow Public Swagger UI endpoint")
    void testPublicSwaggerEndpoint() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/swagger-ui/index.html").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain, times(1)).filter(exchange);
    }

    @Test
    @DisplayName("filter - Missing Authorization header returns 401")
    void testMissingAuthHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/listings").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("filter - Invalid Bearer prefix returns 401")
    void testInvalidBearerPrefix() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/listings")
                .header(HttpHeaders.AUTHORIZATION, "Basic 12345")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("filter - Valid token extracts headers and propagates downstream")
    void testValidToken() {
        String token = Jwts.builder()
                .subject("42")
                .claim("email", "john@campus.edu")
                .claim("role", "STUDENT")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey)
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/listings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        verify(chain, times(1)).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("filter - Expired token returns 401")
    void testExpiredToken() {
        String token = Jwts.builder()
                .subject("42")
                .claim("email", "john@campus.edu")
                .claim("role", "STUDENT")
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(secretKey)
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/listings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    @DisplayName("getOrder - returns -1")
    void testGetOrder() {
        assertEquals(-1, filter.getOrder());
    }
}
