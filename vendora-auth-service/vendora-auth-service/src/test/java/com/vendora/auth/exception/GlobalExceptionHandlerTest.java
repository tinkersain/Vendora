package com.vendora.auth.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/login");
    }

    @Test
    @DisplayName("handleResponseStatus - should return mapped error response")
    void handleResponseStatus_Test() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        ResponseEntity<ErrorResponse> response = handler.handleResponseStatus(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertEquals("/api/auth/login", response.getBody().getPath());
    }

    @Test
    @DisplayName("handleValidationErrors - should return bad request with combined field errors")
    void handleValidationErrors_Test() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("loginRequest", "email", "Email is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("email: Email is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIllegalArgument - should return bad request")
    void handleIllegalArgument_Test() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument supplied");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Illegal argument supplied", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleGeneralException - should return internal server error")
    void handleGeneralException_Test() {
        Exception ex = new RuntimeException("Database timeout");

        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Database timeout", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ErrorResponse - getters, setters and constructors")
    void testErrorResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse err = new ErrorResponse(now, 400, "Bad Request", "Detail message", "/test");

        assertEquals(now, err.getTimestamp());
        assertEquals(400, err.getStatus());
        assertEquals("Bad Request", err.getError());
        assertEquals("Detail message", err.getMessage());
        assertEquals("/test", err.getPath());

        ErrorResponse empty = new ErrorResponse();
        empty.setTimestamp(now);
        empty.setStatus(500);
        empty.setError("Server Error");
        empty.setMessage("Oops");
        empty.setPath("/path");

        assertEquals(500, empty.getStatus());
        assertEquals("Server Error", empty.getError());
    }
}
