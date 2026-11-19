package com.vendora.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;
    // 256-bit base64 test secret
    private final String secret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtYXV0aC1zZXJ2aWNlLXZlbmRvcmEtMjAyNQ==";
    private final long expiration = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expiration);
    }

    @Test
    @DisplayName("generateToken and extractClaims - should encode and decode claims accurately")
    void tokenGenerationAndExtraction_Success() {
        String token = jwtService.generateToken(42L, "user@campus.edu", "STUDENT");

        assertNotNull(token);
        assertEquals(42L, jwtService.extractUserId(token));
        assertEquals("user@campus.edu", jwtService.extractEmail(token));
        assertEquals("STUDENT", jwtService.extractRole(token));
        assertEquals(expiration, jwtService.getExpirationInMillis());
    }

    @Test
    @DisplayName("extractAllClaims - invalid signature should throw exception")
    void invalidToken_ThrowsException() {
        JwtService anotherService = new JwtService("YW5vdGhlci1zZWNyZXQta2V5LWZvci1qd3QtYXV0aC1zZXJ2aWNlLXZlbmRvcmEtMjAyNQ==", expiration);
        String tokenFromAnotherKey = anotherService.generateToken(1L, "fake@campus.edu", "ADMIN");

        assertThrows(Exception.class, () -> jwtService.extractAllClaims(tokenFromAnotherKey));
    }
}
