package com.vendora.marketplace.exception;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/listings/test");
    }

    @Test
    @DisplayName("handleResponseStatus - returns correct error response")
    void testHandleResponseStatus() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResponseStatus(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Listing not found", response.getBody().getMessage());
        assertEquals("/api/listings/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("handleValidationErrors - returns 400 Bad Request")
    void testHandleValidationErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("listingRequest", "title", "Title is required");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("title: Title is required", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIllegalArgument - returns 400 Bad Request")
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid category");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid category", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleGeneralException - returns 500 Internal Server Error")
    void testHandleGeneralException() {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getMessage());
    }

    @Test
    @DisplayName("ErrorResponse getters and setters")
    void testErrorResponse() {
        ErrorResponse res = new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", "Error message", "/path");
        res.setStatus(404);
        res.setError("Not Found");
        res.setMessage("Item not found");
        res.setPath("/new-path");
        res.setTimestamp(LocalDateTime.now());

        assertEquals(404, res.getStatus());
        assertEquals("Not Found", res.getError());
        assertEquals("Item not found", res.getMessage());
        assertEquals("/new-path", res.getPath());
        assertNotNull(res.getTimestamp());
    }
}
