package com.vendora.scrap.controller;

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

import com.vendora.scrap.dto.ScrapAssignmentDto;
import com.vendora.scrap.dto.ScrapRequestDto;
import com.vendora.scrap.dto.ScrapResponseDto;
import com.vendora.scrap.entity.ScrapStatus;
import com.vendora.scrap.service.ScrapService;

@RestController
@RequestMapping("/api/scrap")
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

    // 1. Submit a new scrap collection request (derived from trusted X-User-Id)
    @Operation(summary = "Submit a new scrap collection request (Uses login token)")
    @PostMapping
    public ResponseEntity<ScrapResponseDto> createRequest(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Valid @RequestBody ScrapRequestDto dto) {

        if (headerUserId != null) {
            dto.setUserId(headerUserId);
        }

        if (dto.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID could not be identified from token");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scrapService.createRequest(dto));
    }

    // 2. View my scrap requests (logged-in user)
    @Operation(summary = "View my scrap collection requests (Uses login token)")
    @GetMapping("/my")
    public ResponseEntity<List<ScrapResponseDto>> getMyRequests(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {

        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID could not be identified from token");
        }

        return ResponseEntity.ok(scrapService.getRequestsByUser(currentUserId));
    }

    // 3. Get all scrap requests (ADMIN, COLLECTION_STAFF, or APP_REPRESENTATIVE only)
    @Operation(summary = "Get all campus scrap requests (Staff/Admin only)")
    @GetMapping
    public ResponseEntity<List<ScrapResponseDto>> getAllRequests(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role) && !"COLLECTION_STAFF".equalsIgnoreCase(role) && !"APP_REPRESENTATIVE".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied. As a student, use GET /api/scrap/my to view your own scrap requests.");
        }

        return ResponseEntity.ok(scrapService.getAllRequests());
    }

    // 4. Get a specific scrap request
    @Operation(summary = "Get a specific scrap request by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ScrapResponseDto> getRequestById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        ScrapResponseDto response = scrapService.getRequestById(id);
        if (!"ADMIN".equalsIgnoreCase(role) && !"COLLECTION_STAFF".equalsIgnoreCase(role) && !"APP_REPRESENTATIVE".equalsIgnoreCase(role)
                && (currentUserId == null || !currentUserId.equals(response.getUserId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this scrap request");
        }

        return ResponseEntity.ok(response);
    }

    // 5. Get requests submitted by a specific user (Admin and Staff only)
    @Operation(summary = "Get requests submitted by a specific user (Admin / Staff only)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScrapResponseDto>> getRequestsByUser(
            @PathVariable Long userId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role) && !"COLLECTION_STAFF".equalsIgnoreCase(role) && !"APP_REPRESENTATIVE".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied. As a student, please use GET /api/scrap/my to view your own requests.");
        }

        return ResponseEntity.ok(scrapService.getRequestsByUser(userId));
    }

    // 6. Get requests assigned to a staff member (Admin and Staff only)
    @Operation(summary = "Get requests assigned to a staff member (Admin / Staff only)")
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<ScrapResponseDto>> getRequestsByStaff(
            @PathVariable Long staffId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role) && !"COLLECTION_STAFF".equalsIgnoreCase(role) && !"APP_REPRESENTATIVE".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Access denied. Only collection staff and administrators can view staff assignments.");
        }

        return ResponseEntity.ok(scrapService.getRequestsByStaff(staffId));
    }

    // 7. Assign collection staff to a request (ADMIN only)
    @Operation(summary = "Assign collection staff to a request (Requires ADMIN role)")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<ScrapResponseDto> assignStaff(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role,
            @Valid @RequestBody ScrapAssignmentDto dto) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can assign collection staff");
        }

        return ResponseEntity.ok(scrapService.assignStaff(id, dto));
    }

    // 7. Update request status (COLLECTION_STAFF or ADMIN only)
    @Operation(summary = "Update request status (COLLECTION_STAFF or ADMIN only)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ScrapResponseDto> updateStatus(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long staffId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role,
            @RequestParam ScrapStatus status) {

        if (!"ADMIN".equalsIgnoreCase(role) && !"COLLECTION_STAFF".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only collection staff or administrators can update pickup status");
        }

        return ResponseEntity.ok(scrapService.updateStatus(id, status));
    }

    // 8. Cancel a request
    @Operation(summary = "Cancel a scrap request")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScrapResponseDto> cancelRequest(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Parameter(hidden = true) @RequestParam(value = "userId", required = false) Long paramUserId) {

        Long effectiveUserId = headerUserId != null ? headerUserId : paramUserId;
        return ResponseEntity.ok(scrapService.cancelRequest(id, effectiveUserId));
    }
}
