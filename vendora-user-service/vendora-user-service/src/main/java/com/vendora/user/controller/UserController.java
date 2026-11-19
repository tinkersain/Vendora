package com.vendora.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.vendora.user.dto.UserProfileUpdateRequest;
import com.vendora.user.dto.UserRegistrationRequest;
import com.vendora.user.dto.UserResponse;
import com.vendora.user.entity.AccountStatus;
import com.vendora.user.entity.Role;
import com.vendora.user.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. User Registration API
    @PostMapping("/api/registration/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get current authenticated user profile
    @Operation(summary = "Get current authenticated user profile (Uses Bearer token identity)")
    @GetMapping("/api/users/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long currentUserId) {

        UserResponse response = userService.getUserById(currentUserId);
        return ResponseEntity.ok(response);
    }

    // 3. Update current user profile
    @Operation(summary = "Update current authenticated user profile (Uses Bearer token identity)")
    @PutMapping("/api/users/profile")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long currentUserId,
            @Valid @RequestBody UserProfileUpdateRequest request) {

        UserResponse response = userService.updateUserProfile(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    // 4. Get user profile by ID (Admin and Self see full profile; other students see limited public profile)
    @Operation(summary = "Get user profile by ID (Admin/Self sees full profile; Students see limited public profile)")
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        UserResponse response = userService.getUserById(id);

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isSelf = currentUserId != null && currentUserId.equals(id);

        if (!isAdmin && !isSelf) {
            // Mask private/sensitive information for public student view
            response.setPhone(null);
            response.setRegistrationNumber(null);
            response.setAccountStatus(null);
        }

        return ResponseEntity.ok(response);
    }

    // 5. Admin update user account status (ACTIVE, SUSPENDED)
    @Operation(summary = "Admin update user account status (Requires ADMIN role)")
    @PatchMapping("/api/users/{id}/status")
    public ResponseEntity<UserResponse> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can update account status");
        }

        UserResponse response = userService.updateAccountStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // 6. Update user role (e.g., ADMIN, STUDENT, FACULTY, APP_REPRESENTATIVE) - ADMIN only
    @Operation(summary = "Update user role (Requires ADMIN role)")
    @PatchMapping("/api/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String callerRole) {

        if (!"ADMIN".equalsIgnoreCase(callerRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can change user roles");
        }

        UserResponse response = userService.updateUserRole(id, role);
        return ResponseEntity.ok(response);
    }
}
