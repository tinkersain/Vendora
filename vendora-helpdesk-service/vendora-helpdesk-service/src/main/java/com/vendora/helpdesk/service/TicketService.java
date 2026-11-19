package com.vendora.helpdesk.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.helpdesk.dto.CommentCreateRequest;
import com.vendora.helpdesk.dto.CommentResponse;
import com.vendora.helpdesk.dto.TicketAssignRequest;
import com.vendora.helpdesk.dto.TicketCreateRequest;
import com.vendora.helpdesk.dto.TicketResponse;
import com.vendora.helpdesk.dto.TicketStatusUpdateRequest;
import com.vendora.helpdesk.entity.Ticket;
import com.vendora.helpdesk.entity.TicketCategory;
import com.vendora.helpdesk.entity.TicketComment;
import com.vendora.helpdesk.entity.TicketPriority;
import com.vendora.helpdesk.entity.TicketStatus;
import com.vendora.helpdesk.repository.TicketCommentRepository;
import com.vendora.helpdesk.repository.TicketRepository;

@Service
@Transactional
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository, TicketCommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Create a new facility / maintenance ticket.
     */
    public TicketResponse createTicket(Long userId, TicketCreateRequest request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication required");
        }

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setUserId(userId);
        ticket.setTitle(request.getTitle().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setLocation(request.getLocation().trim());

        Ticket saved = ticketRepository.save(ticket);
        logger.info("Created new helpdesk ticket #{} (ID: {}) by user ID {}", saved.getTicketNumber(), saved.getId(), userId);

        return TicketResponse.fromEntity(saved, 0);
    }

    /**
     * Get tickets raised by the current user.
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication required");
        }
        return ticketRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(t -> TicketResponse.fromEntity(t, commentRepository.countByTicketId(t.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Get all tickets with optional filtering (ADMIN & MAINTENANCE_STAFF only).
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets(TicketStatus status, TicketCategory category, Long staffId, String role) {
        if (!isAdminOrStaff(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Staff or Admin role required");
        }

        List<Ticket> tickets = ticketRepository.findAllByOrderByCreatedAtDesc();

        return tickets.stream()
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> category == null || t.getCategory() == category)
                .filter(t -> staffId == null || (t.getAssignedStaffId() != null && t.getAssignedStaffId().equals(staffId)))
                .map(t -> TicketResponse.fromEntity(t, commentRepository.countByTicketId(t.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Get ticket by ID with role-based access check.
     */
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id, Long currentUserId, String role) {
        Ticket ticket = findTicketOrThrow(id);

        if (!isAdminOrStaff(role) && !ticket.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You can only view your own tickets");
        }

        int commentCount = commentRepository.countByTicketId(ticket.getId());
        return TicketResponse.fromEntity(ticket, commentCount);
    }

    /**
     * Assign maintenance staff to ticket (ADMIN or MAINTENANCE_STAFF only).
     */
    public TicketResponse assignStaff(Long ticketId, TicketAssignRequest request, Long currentUserId, String role) {
        if (!isAdminOrStaff(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Staff or Admin role required to assign tickets");
        }

        Ticket ticket = findTicketOrThrow(ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot assign staff to a ticket that is " + ticket.getStatus());
        }

        ticket.setAssignedStaffId(request.getStaffId());
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        }

        Ticket updated = ticketRepository.save(ticket);
        logger.info("Ticket #{} assigned to staff ID {} by user ID {}", updated.getTicketNumber(), request.getStaffId(), currentUserId);

        // System comment for audit trail
        TicketComment auditComment = new TicketComment(
                ticketId,
                currentUserId,
                role != null ? role : "SYSTEM",
                "Ticket assigned to staff member (User ID: " + request.getStaffId() + ")",
                false
        );
        commentRepository.save(auditComment);

        return TicketResponse.fromEntity(updated, commentRepository.countByTicketId(updated.getId()));
    }

    /**
     * Update ticket status with lifecycle validation.
     */
    public TicketResponse updateStatus(Long ticketId, TicketStatusUpdateRequest request, Long currentUserId, String role) {
        Ticket ticket = findTicketOrThrow(ticketId);
        TicketStatus newStatus = request.getStatus();
        TicketStatus oldStatus = ticket.getStatus();

        if (oldStatus == newStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ticket is already in status " + oldStatus);
        }

        // Terminal states cannot transition
        if (oldStatus == TicketStatus.CLOSED || oldStatus == TicketStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot change status of a " + oldStatus + " ticket");
        }

        // Verify authorization for requested status
        if ("STUDENT".equalsIgnoreCase(role)) {
            if (!ticket.getUserId().equals(currentUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this ticket");
            }
            // Students can only cancel their open/assigned ticket, or close a resolved ticket
            if (newStatus == TicketStatus.CANCELLED) {
                if (oldStatus != TicketStatus.OPEN && oldStatus != TicketStatus.ASSIGNED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "You can only cancel tickets that are OPEN or ASSIGNED");
                }
            } else if (newStatus == TicketStatus.CLOSED) {
                if (oldStatus != TicketStatus.RESOLVED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "You can only close tickets that are marked as RESOLVED");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Students can only cancel or close resolved tickets");
            }
        } else if (!isAdminOrStaff(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Unauthorized role");
        }

        // State Machine validation
        validateStatusTransition(oldStatus, newStatus);

        ticket.setStatus(newStatus);
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            ticket.setResolutionNotes(request.getResolutionNotes().trim());
        }

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        Ticket saved = ticketRepository.save(ticket);
        logger.info("Ticket #{} status transitioned from {} to {} by user ID {}",
                ticket.getTicketNumber(), oldStatus, newStatus, currentUserId);

        // Record status change comment
        String note = "Status updated to " + newStatus;
        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            note += " - Note: " + request.getResolutionNotes().trim();
        }
        TicketComment audit = new TicketComment(ticketId, currentUserId, role, note, false);
        commentRepository.save(audit);

        return TicketResponse.fromEntity(saved, commentRepository.countByTicketId(saved.getId()));
    }

    /**
     * Cancel a ticket (Owner or Admin).
     */
    public TicketResponse cancelTicket(Long ticketId, Long currentUserId, String role) {
        TicketStatusUpdateRequest cancelRequest = new TicketStatusUpdateRequest(
                TicketStatus.CANCELLED, "Cancelled by user"
        );
        return updateStatus(ticketId, cancelRequest, currentUserId, role);
    }

    /**
     * Add a comment to a ticket thread.
     */
    public CommentResponse addComment(Long ticketId, CommentCreateRequest request, Long currentUserId, String role) {
        Ticket ticket = findTicketOrThrow(ticketId);

        boolean isStaffOrAdmin = isAdminOrStaff(role);
        boolean isOwner = ticket.getUserId().equals(currentUserId);

        if (!isStaffOrAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only comment on your own tickets");
        }

        if (Boolean.TRUE.equals(request.getIsInternalNote()) && !isStaffOrAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only staff and administrators can post internal notes");
        }

        if (ticket.getStatus() == TicketStatus.CLOSED || ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add comments to a " + ticket.getStatus() + " ticket");
        }

        TicketComment comment = new TicketComment(
                ticketId,
                currentUserId,
                role != null ? role : "STUDENT",
                request.getMessage().trim(),
                request.getIsInternalNote() != null ? request.getIsInternalNote() : false
        );

        TicketComment saved = commentRepository.save(comment);
        logger.info("Added comment ID {} to ticket ID {} by user ID {}", saved.getId(), ticketId, currentUserId);

        return CommentResponse.fromEntity(saved);
    }

    /**
     * Get comments for a ticket (internal notes filtered for regular students).
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long ticketId, Long currentUserId, String role) {
        Ticket ticket = findTicketOrThrow(ticketId);

        boolean isStaffOrAdmin = isAdminOrStaff(role);
        boolean isOwner = ticket.getUserId().equals(currentUserId);

        if (!isStaffOrAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view comments on your own tickets");
        }

        List<TicketComment> comments;
        if (isStaffOrAdmin) {
            comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        } else {
            comments = commentRepository.findByTicketIdAndIsInternalNoteFalseOrderByCreatedAtAsc(ticketId);
        }

        return comments.stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Helper methods
    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Helpdesk ticket not found with ID: " + id));
    }

    private boolean isAdminOrStaff(String role) {
        return "ADMIN".equalsIgnoreCase(role) || "MAINTENANCE_STAFF".equalsIgnoreCase(role);
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus target) {
        boolean valid = switch (current) {
            case OPEN -> target == TicketStatus.ASSIGNED || target == TicketStatus.IN_PROGRESS || target == TicketStatus.CANCELLED;
            case ASSIGNED -> target == TicketStatus.IN_PROGRESS || target == TicketStatus.OPEN || target == TicketStatus.CANCELLED;
            case IN_PROGRESS -> target == TicketStatus.RESOLVED || target == TicketStatus.ASSIGNED;
            case RESOLVED -> target == TicketStatus.CLOSED || target == TicketStatus.IN_PROGRESS;
            case CLOSED, CANCELLED -> false;
        };

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Invalid status transition from %s to %s", current, target));
        }
    }

    private String generateTicketNumber() {
        return "HD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
