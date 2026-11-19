package com.vendora.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vendora.auth.dto.LoginRequest;
import com.vendora.auth.dto.LoginResponse;
import com.vendora.auth.dto.TokenValidationResponse;
import com.vendora.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@campus.edu", "Password@123");
        loginResponse = new LoginResponse("mock-jwt-token", 1L, "test@campus.edu", "STUDENT", 3600L);
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 200 and LoginResponse on valid credentials")
    void login_Success() {
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loginResponse, response.getBody());
        assertEquals("mock-jwt-token", response.getBody().getAccessToken());
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should return 200 when token is valid")
    void validateToken_Valid() {
        TokenValidationResponse validationResponse = TokenValidationResponse.valid(1L, "test@campus.edu", "STUDENT");
        when(authService.validateToken("Bearer valid-token")).thenReturn(validationResponse);

        ResponseEntity<TokenValidationResponse> response = authController.validateToken("Bearer valid-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().isValid());
        assertEquals(1L, response.getBody().getUserId());
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should return 401 when token is invalid")
    void validateToken_Invalid() {
        TokenValidationResponse validationResponse = TokenValidationResponse.invalid("Token expired or signature invalid");
        when(authService.validateToken("Bearer invalid-token")).thenReturn(validationResponse);

        ResponseEntity<TokenValidationResponse> response = authController.validateToken("Bearer invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(false, response.getBody().isValid());
    }
}
