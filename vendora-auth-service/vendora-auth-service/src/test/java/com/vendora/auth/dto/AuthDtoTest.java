package com.vendora.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthDtoTest {

    @Test
    @DisplayName("LoginRequest - getters, setters, constructors")
    void testLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setEmail("student@campus.edu");
        req.setPassword("Secret@123");

        assertEquals("student@campus.edu", req.getEmail());
        assertEquals("Secret@123", req.getPassword());

        LoginRequest req2 = new LoginRequest("admin@campus.edu", "Admin@123");
        assertEquals("admin@campus.edu", req2.getEmail());
        assertEquals("Admin@123", req2.getPassword());
    }

    @Test
    @DisplayName("LoginResponse - getters, constructor")
    void testLoginResponse() {
        LoginResponse resp = new LoginResponse("token123", 10L, "user@campus.edu", "STUDENT", 3600L);

        assertEquals("token123", resp.getAccessToken());
        assertEquals("Bearer", resp.getTokenType());
        assertEquals(10L, resp.getUserId());
        assertEquals("user@campus.edu", resp.getEmail());
        assertEquals("STUDENT", resp.getRole());
        assertEquals(3600L, resp.getExpiresIn());
    }

    @Test
    @DisplayName("TokenValidationResponse - factory methods, getters, setters")
    void testTokenValidationResponse() {
        TokenValidationResponse validResp = TokenValidationResponse.valid(5L, "valid@campus.edu", "ADMIN");
        assertTrue(validResp.isValid());
        assertEquals(5L, validResp.getUserId());
        assertEquals("valid@campus.edu", validResp.getEmail());
        assertEquals("ADMIN", validResp.getRole());
        assertEquals("Token is valid", validResp.getMessage());

        TokenValidationResponse invalidResp = TokenValidationResponse.invalid("Expired");
        assertFalse(invalidResp.isValid());
        assertNull(invalidResp.getUserId());
        assertEquals("Expired", invalidResp.getMessage());

        TokenValidationResponse custom = new TokenValidationResponse();
        custom.setValid(true);
        custom.setUserId(99L);
        custom.setEmail("custom@campus.edu");
        custom.setRole("FACULTY");
        custom.setMessage("Custom OK");

        assertTrue(custom.isValid());
        assertEquals(99L, custom.getUserId());
        assertEquals("custom@campus.edu", custom.getEmail());
        assertEquals("FACULTY", custom.getRole());
        assertEquals("Custom OK", custom.getMessage());
    }

    @Test
    @DisplayName("AuthUserDetails - getters and setters")
    void testAuthUserDetails() {
        AuthUserDetails details = new AuthUserDetails();
        details.setId(100L);
        details.setEmail("staff@campus.edu");
        details.setPasswordHash("hashed_pwd");
        details.setRole("MAINTENANCE_STAFF");
        details.setAccountStatus("ACTIVE");

        assertEquals(100L, details.getId());
        assertEquals("staff@campus.edu", details.getEmail());
        assertEquals("hashed_pwd", details.getPasswordHash());
        assertEquals("MAINTENANCE_STAFF", details.getRole());
        assertEquals("ACTIVE", details.getAccountStatus());
    }
}
