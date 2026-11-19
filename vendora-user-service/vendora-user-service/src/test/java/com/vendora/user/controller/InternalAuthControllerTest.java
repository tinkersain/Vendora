package com.vendora.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.AuthUserDetailsResponse;
import com.vendora.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class InternalAuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private InternalAuthController controller;

    private final String secret = "vendora-internal-secret-2025";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "serviceSecret", secret);
    }

    @Test
    @DisplayName("getAuthDetails - Should return user details on matching secret")
    void getAuthDetails_Success() {
        AuthUserDetailsResponse expected = new AuthUserDetailsResponse(1L, "user@campus.edu", "hashedpass", "STUDENT", "ACTIVE");

        when(userService.getAuthUserDetails("user@campus.edu")).thenReturn(expected);

        AuthUserDetailsResponse resp = controller.getAuthDetails("user@campus.edu", secret);

        assertEquals("user@campus.edu", resp.getEmail());
        assertEquals("STUDENT", resp.getRole());
        assertEquals(1L, resp.getId());
    }

    @Test
    @DisplayName("getAuthDetails - Should throw 403 on invalid secret")
    void getAuthDetails_InvalidSecret() {
        assertThrows(ResponseStatusException.class,
                () -> controller.getAuthDetails("user@campus.edu", "wrong-secret"));
    }
}
