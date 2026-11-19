package com.vendora.scrap.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "VmVuZG9yYUxvY2FsSnd0U2VjcmV0S2V5MzJCeXRlcyE=";
    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    @Test
    void testExtractClaims_Success() {
        String token = Jwts.builder()
                .subject("42")
                .claim("email", "student@vendora.com")
                .claim("role", "STUDENT")
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key)
                .compact();

        assertEquals("42", jwtService.extractUserId(token));
        assertEquals("student@vendora.com", jwtService.extractEmail(token));
        assertEquals("STUDENT", jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void testIsTokenValid_InvalidToken_ReturnsFalse() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }

    @Test
    void testIsTokenValid_ExpiredToken_ReturnsFalse() {
        String token = Jwts.builder()
                .subject("42")
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(key)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }
}
