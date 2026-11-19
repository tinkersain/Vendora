package com.vendora.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.web.server.ResponseStatusException;

import com.vendora.user.dto.UserProfileUpdateRequest;
import com.vendora.user.dto.UserRegistrationRequest;
import com.vendora.user.dto.UserResponse;
import com.vendora.user.entity.AccountStatus;
import com.vendora.user.entity.Role;
import com.vendora.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new UserResponse();
        sampleUser.setId(10L);
        sampleUser.setFullName("Aarav Sharma");
        sampleUser.setEmail("aarav@campus.edu");
        sampleUser.setPhone("9876543210");
        sampleUser.setRegistrationNumber("REG-12345");
        sampleUser.setRole(Role.STUDENT);
        sampleUser.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("registerUser - should return 201 Created and UserResponse")
    void registerUser_Success() {
        UserRegistrationRequest req = new UserRegistrationRequest();
        req.setFullName("Aarav Sharma");
        req.setEmail("aarav@campus.edu");
        req.setPhone("9876543210");
        req.setPassword("Password@123");

        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.registerUser(req);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(sampleUser, resp.getBody());
    }

    @Test
    @DisplayName("getCurrentUserProfile - should return user profile for authenticated user")
    void getCurrentUserProfile_Success() {
        when(userService.getUserById(10L)).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.getCurrentUserProfile(10L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(10L, resp.getBody().getId());
    }

    @Test
    @DisplayName("updateCurrentUserProfile - should update and return profile")
    void updateCurrentUserProfile_Success() {
        UserProfileUpdateRequest updateReq = new UserProfileUpdateRequest();
        updateReq.setFullName("Aarav S.");
        updateReq.setPhone("9876543211");

        when(userService.updateUserProfile(eq(10L), any(UserProfileUpdateRequest.class))).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.updateCurrentUserProfile(10L, updateReq);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("getUserById - Admin should see full unmasked profile")
    void getUserById_Admin() {
        when(userService.getUserById(10L)).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.getUserById(10L, 99L, "ADMIN");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("9876543210", resp.getBody().getPhone());
    }

    @Test
    @DisplayName("getUserById - Other student should see masked profile (phone null)")
    void getUserById_OtherStudent() {
        when(userService.getUserById(10L)).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.getUserById(10L, 99L, "STUDENT");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNull(resp.getBody().getPhone());
        assertNull(resp.getBody().getRegistrationNumber());
    }

    @Test
    @DisplayName("updateAccountStatus - Admin should update status")
    void updateAccountStatus_Admin() {
        when(userService.updateAccountStatus(10L, AccountStatus.SUSPENDED)).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.updateAccountStatus(10L, AccountStatus.SUSPENDED, "ADMIN");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("updateAccountStatus - Non-admin should throw 403 Forbidden")
    void updateAccountStatus_Forbidden() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.updateAccountStatus(10L, AccountStatus.SUSPENDED, "STUDENT"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("updateUserRole - Admin should update role")
    void updateUserRole_Admin() {
        when(userService.updateUserRole(10L, Role.MAINTENANCE_STAFF)).thenReturn(sampleUser);

        ResponseEntity<UserResponse> resp = userController.updateUserRole(10L, Role.MAINTENANCE_STAFF, "ADMIN");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    @DisplayName("updateUserRole - Non-admin should throw 403 Forbidden")
    void updateUserRole_Forbidden() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userController.updateUserRole(10L, Role.MAINTENANCE_STAFF, "STUDENT"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
