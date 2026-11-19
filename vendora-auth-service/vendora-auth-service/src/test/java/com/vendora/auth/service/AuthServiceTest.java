package com.vendora.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.auth.client.UserServiceClient;
import com.vendora.auth.dto.AuthUserDetails;
import com.vendora.auth.dto.LoginRequest;
import com.vendora.auth.dto.LoginResponse;
import com.vendora.auth.dto.TokenValidationResponse;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import java.util.Collections;

class AuthServiceTest {

    private UserServiceClient userServiceClient;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userServiceClient = mock(UserServiceClient.class);
        jwtService = mock(JwtService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthService(userServiceClient, jwtService, passwordEncoder);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@vendora.com");
        req.setPassword("Password123");

        AuthUserDetails user = new AuthUserDetails();
        user.setId(1L);
        user.setEmail("test@vendora.com");
        user.setPasswordHash("hashed_pass");
        user.setRole("STUDENT");
        user.setAccountStatus("ACTIVE");

        when(userServiceClient.getUserByEmail(eq("test@vendora.com"), anyString())).thenReturn(user);
        when(passwordEncoder.matches("Password123", "hashed_pass")).thenReturn(true);
        when(jwtService.generateToken(1L, "test@vendora.com", "STUDENT")).thenReturn("mock-jwt-token");
        when(jwtService.getExpirationInMillis()).thenReturn(3600000L);

        LoginResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("mock-jwt-token", resp.getAccessToken());
        assertEquals(1L, resp.getUserId());
        assertEquals("test@vendora.com", resp.getEmail());
        assertEquals("STUDENT", resp.getRole());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@vendora.com");
        req.setPassword("WrongPassword");

        AuthUserDetails user = new AuthUserDetails();
        user.setId(1L);
        user.setEmail("test@vendora.com");
        user.setPasswordHash("hashed_pass");
        user.setRole("STUDENT");
        user.setAccountStatus("ACTIVE");

        when(userServiceClient.getUserByEmail(eq("test@vendora.com"), anyString())).thenReturn(user);
        when(passwordEncoder.matches("WrongPassword", "hashed_pass")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginInactiveAccount() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@vendora.com");
        req.setPassword("Password123");

        AuthUserDetails user = new AuthUserDetails();
        user.setId(1L);
        user.setEmail("test@vendora.com");
        user.setPasswordHash("hashed_pass");
        user.setRole("STUDENT");
        user.setAccountStatus("SUSPENDED");

        when(userServiceClient.getUserByEmail(eq("test@vendora.com"), anyString())).thenReturn(user);
        when(passwordEncoder.matches("Password123", "hashed_pass")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("unknown@vendora.com");
        req.setPassword("Password123");

        Request feignReq = Request.create(Request.HttpMethod.GET, "/internal/users/auth-details", Collections.emptyMap(), null, new RequestTemplate());
        when(userServiceClient.getUserByEmail(eq("unknown@vendora.com"), anyString()))
                .thenThrow(new FeignException.NotFound("Not found", feignReq, null, null));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginServiceUnavailable() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@vendora.com");
        req.setPassword("Password123");

        when(userServiceClient.getUserByEmail(anyString(), anyString())).thenThrow(new RuntimeException("Connection refused"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void testValidateTokenValid() {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("10");
        when(claims.get("email", String.class)).thenReturn("valid@vendora.com");
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        when(jwtService.extractAllClaims("valid_token")).thenReturn(claims);

        TokenValidationResponse resp = authService.validateToken("Bearer valid_token");

        assertTrue(resp.isValid());
        assertEquals(10L, resp.getUserId());
        assertEquals("valid@vendora.com", resp.getEmail());
        assertEquals("ADMIN", resp.getRole());
    }

    @Test
    void testValidateTokenInvalidHeader() {
        TokenValidationResponse resp1 = authService.validateToken(null);
        assertFalse(resp1.isValid());

        TokenValidationResponse resp2 = authService.validateToken("Basic 123");
        assertFalse(resp2.isValid());
    }

    @Test
    void testValidateTokenExpiredOrCorrupted() {
        when(jwtService.extractAllClaims("expired_token")).thenThrow(new JwtException("Token expired"));

        TokenValidationResponse resp = authService.validateToken("Bearer expired_token");
        assertFalse(resp.isValid());
    }
}
