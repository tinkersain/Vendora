package com.vendora.helpdesk.controller;

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

import com.vendora.helpdesk.dto.CommentCreateRequest;
import com.vendora.helpdesk.dto.CommentResponse;
import com.vendora.helpdesk.dto.TicketAssignRequest;
import com.vendora.helpdesk.dto.TicketCreateRequest;
import com.vendora.helpdesk.dto.TicketResponse;
import com.vendora.helpdesk.dto.TicketStatusUpdateRequest;
import com.vendora.helpdesk.entity.TicketCategory;
import com.vendora.helpdesk.entity.TicketStatus;
import com.vendora.helpdesk.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/helpdesk/tickets")
@Tag(name = "Helpdesk Ticket Controller", description = "Campus facility and maintenance ticketing system")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Operation(summary = "Create a new helpdesk ticket", description = "Student or staff submits an issue for lab, electrical, wifi, etc.")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketCreateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId) {

        TicketResponse response = ticketService.createTicket(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Get all tickets submitted by the logged-in user", description = "Returns active and past tickets raised by current user")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId) {

        List<TicketResponse> responses = ticketService.getMyTickets(currentUserId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all tickets with optional filtering", description = "Staff or Admin access only. Filter by status, category, or staff ID")
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(required = false) Long staffId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        List<TicketResponse> responses = ticketService.getAllTickets(status, category, staffId, role);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get details of a specific ticket by ID", description = "Accessible by ticket owner, assigned staff, or Admin")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        TicketResponse response = ticketService.getTicketById(id, currentUserId, role);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign maintenance staff member to ticket", description = "Staff or Admin only. Transitions OPEN tickets to ASSIGNED")
    public ResponseEntity<TicketResponse> assignStaff(
            @PathVariable Long id,
            @Valid @RequestBody TicketAssignRequest request,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        TicketResponse response = ticketService.assignStaff(id, request, currentUserId, role);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update ticket lifecycle status", description = "Transitions between OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        TicketResponse response = ticketService.updateStatus(id, request, currentUserId, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a ticket", description = "Allowed for ticket owner (if OPEN/ASSIGNED) or Admin")
    public ResponseEntity<TicketResponse> cancelTicket(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        TicketResponse response = ticketService.cancelTicket(id, currentUserId, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment or update note to a ticket", description = "Add messages. Staff can set isInternalNote=true for private notes")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        CommentResponse response = ticketService.addComment(id, request, currentUserId, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Get conversation history and notes for a ticket", description = "Retrieves notes. Internal notes hidden from non-staff students")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) @Parameter(hidden = true) Long currentUserId,
            @RequestHeader(value = "X-User-Role", required = false) @Parameter(hidden = true) String role) {

        List<CommentResponse> responses = ticketService.getComments(id, currentUserId, role);
        return ResponseEntity.ok(responses);
    }
}
