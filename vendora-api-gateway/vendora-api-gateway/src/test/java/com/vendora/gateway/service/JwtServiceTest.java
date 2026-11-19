package com.vendora.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private final String secret = "VmVuZG9yYUxvY2FsSnd0U2VjcmV0S2V5MzJCeXRlcyE=";
    private JwtService jwtService;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret);
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void testValidateTokenAndExtractClaims() {
        String token = Jwts.builder()
                .subject("123")
                .claim("email", "student@vendora.com")
                .claim("role", "STUDENT")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey)
                .compact();

        Claims claims = jwtService.validateToken(token);
        assertNotNull(claims);
        assertEquals(123L, jwtService.extractUserId(claims));
        assertEquals("student@vendora.com", jwtService.extractEmail(claims));
        assertEquals("STUDENT", jwtService.extractRole(claims));
    }

    @Test
    void testValidateExpiredTokenThrows() {
        String token = Jwts.builder()
                .subject("123")
                .claim("email", "expired@vendora.com")
                .claim("role", "STUDENT")
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(secretKey)
                .compact();

        assertThrows(JwtException.class, () -> jwtService.validateToken(token));
    }
}
