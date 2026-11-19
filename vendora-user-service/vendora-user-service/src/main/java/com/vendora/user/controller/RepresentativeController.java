package com.vendora.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.vendora.user.dto.RepresentativeApplicationRequest;
import com.vendora.user.dto.RepresentativeResponse;
import com.vendora.user.service.RepresentativeService;

@RestController
@RequestMapping("/api/users/representative")
public class RepresentativeController {

    private final RepresentativeService representativeService;

    public RepresentativeController(RepresentativeService representativeService) {
        this.representativeService = representativeService;
    }

    // 1. Submit application for representative role
    @Operation(summary = "Submit application for representative role (Uses Bearer token)")
    @PostMapping("/apply")
    public ResponseEntity<RepresentativeResponse> apply(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long currentUserId,
            @Valid @RequestBody(required = false) RepresentativeApplicationRequest request) {

        RepresentativeResponse response = representativeService.apply(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get current user's representative application status
    @Operation(summary = "Get current user's representative application status (Uses Bearer token)")
    @GetMapping("/status")
    public ResponseEntity<RepresentativeResponse> getMyStatus(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(representativeService.getByUserId(currentUserId));
    }

    // 3. Admin review of application (APPROVE / REJECT)
    @Operation(summary = "Admin review of application (Requires ADMIN role)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<RepresentativeResponse> reviewApplication(
            @PathVariable Long id,
            @RequestParam String status,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long adminId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can review applications");
        }

        RepresentativeResponse response = representativeService.review(id, adminId, status);
        return ResponseEntity.ok(response);
    }

    // 4. Admin view all representative applications
    @Operation(summary = "Admin view all representative applications (Requires ADMIN role)")
    @GetMapping("/all")
    public ResponseEntity<List<RepresentativeResponse>> getAllApplications(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can view all applications");
        }

        return ResponseEntity.ok(representativeService.getAll());
    }
}
