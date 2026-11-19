package com.vendora.helpdesk.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketCommentRepository commentRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        sampleTicket = new Ticket();
        sampleTicket.setId(10L);
        sampleTicket.setTicketNumber("HD-ABC12345");
        sampleTicket.setUserId(5L);
        sampleTicket.setTitle("Broken Projector in Lab 3");
        sampleTicket.setDescription("HDMI port does not transmit video signal.");
        sampleTicket.setCategory(TicketCategory.LAB_EQUIPMENT);
        sampleTicket.setPriority(TicketPriority.HIGH);
        sampleTicket.setStatus(TicketStatus.OPEN);
        sampleTicket.setLocation("Science Block, Lab 304");
        sampleTicket.setCreatedAt(LocalDateTime.now());
        sampleTicket.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create ticket successfully with OPEN status and generated ticket number")
    void createTicket_Success() {
        TicketCreateRequest request = new TicketCreateRequest(
                "Broken Projector",
                "HDMI not working",
                TicketCategory.LAB_EQUIPMENT,
                TicketPriority.HIGH,
                "Room 304"
        );

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TicketResponse res = ticketService.createTicket(5L, request);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Broken Projector", res.getTitle());
        assertEquals(TicketStatus.OPEN, res.getStatus());
        assertEquals(5L, res.getUserId());
        assertNotNull(res.getTicketNumber());
        assertTrue(res.getTicketNumber().startsWith("HD-"));
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw 401 when creating ticket without authentication")
    void createTicket_Unauthenticated_ThrowsUnauthorized() {
        TicketCreateRequest request = new TicketCreateRequest(
                "Wifi Issue", "No internet", TicketCategory.INTERNET_WIFI, TicketPriority.MEDIUM, "Hostel 2"
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.createTicket(null, request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should return tickets created by current user")
    void getMyTickets_Success() {
        when(ticketRepository.findByUserIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(sampleTicket));
        when(commentRepository.countByTicketId(10L)).thenReturn(2);

        List<TicketResponse> tickets = ticketService.getMyTickets(5L);

        assertEquals(1, tickets.size());
        assertEquals(10L, tickets.get(0).getId());
        assertEquals(2, tickets.get(0).getCommentCount());
    }

    @Test
    @DisplayName("Should return all tickets when requested by Admin")
    void getAllTickets_AsAdmin_Success() {
        when(ticketRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleTicket));
        when(commentRepository.countByTicketId(10L)).thenReturn(0);

        List<TicketResponse> result = ticketService.getAllTickets(null, null, null, "ADMIN");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should throw 403 Forbidden when Student attempts to view all tickets")
    void getAllTickets_AsStudent_Forbidden() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.getAllTickets(null, null, null, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should retrieve ticket by ID when caller is ticket owner")
    void getTicketById_AsOwner_Success() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(commentRepository.countByTicketId(10L)).thenReturn(1);

        TicketResponse res = ticketService.getTicketById(10L, 5L, "STUDENT");

        assertEquals(10L, res.getId());
        assertEquals(1, res.getCommentCount());
    }

    @Test
    @DisplayName("Should throw 403 Forbidden when another student views a ticket")
    void getTicketById_AsOtherStudent_Forbidden() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.getTicketById(10L, 99L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 404 when ticket does not exist")
    void getTicketById_NotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.getTicketById(999L, 5L, "ADMIN"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should assign staff and transition status from OPEN to ASSIGNED")
    void assignStaff_Success() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketAssignRequest req = new TicketAssignRequest(25L);
        TicketResponse res = ticketService.assignStaff(10L, req, 3L, "ADMIN");

        assertEquals(25L, res.getAssignedStaffId());
        assertEquals(TicketStatus.ASSIGNED, res.getStatus());
        verify(commentRepository).save(any(TicketComment.class));
    }

    @Test
    @DisplayName("Should throw 403 when Student tries to assign staff")
    void assignStaff_AsStudent_Forbidden() {
        TicketAssignRequest req = new TicketAssignRequest(25L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.assignStaff(10L, req, 5L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should transition status from OPEN to IN_PROGRESS")
    void updateStatus_OpenToInProgress_Success() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.IN_PROGRESS, "Work starting");
        TicketResponse res = ticketService.updateStatus(10L, req, 3L, "ADMIN");

        assertEquals(TicketStatus.IN_PROGRESS, res.getStatus());
    }

    @Test
    @DisplayName("Should transition status to RESOLVED and record resolvedAt timestamp")
    void updateStatus_InProgressToResolved_SetsResolvedAt() {
        sampleTicket.setStatus(TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.RESOLVED, "Cable replaced");
        TicketResponse res = ticketService.updateStatus(10L, req, 3L, "MAINTENANCE_STAFF");

        assertEquals(TicketStatus.RESOLVED, res.getStatus());
        assertEquals("Cable replaced", res.getResolutionNotes());
        assertNotNull(res.getResolvedAt());
    }

    @Test
    @DisplayName("Should allow student owner to CLOSE a resolved ticket")
    void updateStatus_ResolvedToClosed_AsStudent_Success() {
        sampleTicket.setStatus(TicketStatus.RESOLVED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.CLOSED, "Verified working");
        TicketResponse res = ticketService.updateStatus(10L, req, 5L, "STUDENT");

        assertEquals(TicketStatus.CLOSED, res.getStatus());
    }

    @Test
    @DisplayName("Should throw 400 when attempting invalid transition (OPEN -> CLOSED)")
    void updateStatus_InvalidTransition_ThrowsBadRequest() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.CLOSED, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 3L, "ADMIN"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Invalid status transition"));
    }

    @Test
    @DisplayName("Should throw 400 when updating to identical status")
    void updateStatus_SameStatus_ThrowsBadRequest() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.OPEN, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 3L, "ADMIN"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should allow owner to cancel open ticket")
    void cancelTicket_Success() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse res = ticketService.cancelTicket(10L, 5L, "STUDENT");

        assertEquals(TicketStatus.CANCELLED, res.getStatus());
    }

    @Test
    @DisplayName("Should add comment to ticket thread")
    void addComment_Success() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(invocation -> {
            TicketComment c = invocation.getArgument(0);
            c.setId(101L);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        CommentCreateRequest req = new CommentCreateRequest("Please check power cord too", false);
        CommentResponse res = ticketService.addComment(10L, req, 5L, "STUDENT");

        assertNotNull(res);
        assertEquals(101L, res.getId());
        assertEquals("Please check power cord too", res.getMessage());
        assertEquals(5L, res.getSenderId());
    }

    @Test
    @DisplayName("Should throw 403 when student tries to post internal note")
    void addComment_InternalNote_ByStudent_Forbidden() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        CommentCreateRequest req = new CommentCreateRequest("Internal staff notes", true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.addComment(10L, req, 5L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should filter out internal notes when student views ticket comments")
    void getComments_ByOwner_FiltersInternalNotes() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketComment publicComment = new TicketComment(10L, 5L, "STUDENT", "Public message", false);
        publicComment.setId(1L);
        publicComment.setCreatedAt(LocalDateTime.now());

        when(commentRepository.findByTicketIdAndIsInternalNoteFalseOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(publicComment));

        List<CommentResponse> comments = ticketService.getComments(10L, 5L, "STUDENT");

        assertEquals(1, comments.size());
        assertFalse(comments.get(0).getIsInternalNote());
    }

    @Test
    @DisplayName("Should include internal notes when admin views comments")
    void getComments_ByAdmin_IncludesInternalNotes() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketComment c1 = new TicketComment(10L, 5L, "STUDENT", "Public message", false);
        TicketComment c2 = new TicketComment(10L, 3L, "ADMIN", "Internal technician note", true);
        c1.setId(1L);
        c2.setId(2L);
        c1.setCreatedAt(LocalDateTime.now());
        c2.setCreatedAt(LocalDateTime.now());

        when(commentRepository.findByTicketIdOrderByCreatedAtAsc(10L)).thenReturn(List.of(c1, c2));

        List<CommentResponse> comments = ticketService.getComments(10L, 3L, "ADMIN");

        assertEquals(2, comments.size());
    }

    @Test
    @DisplayName("Should throw 404 when ticket is not found")
    void findTicket_NotFound_ThrowsNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.getTicketById(999L, 5L, "STUDENT"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 400 when assigning staff to closed ticket")
    void assignStaff_ClosedTicket_ThrowsBadRequest() {
        sampleTicket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketAssignRequest req = new TicketAssignRequest(2L);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.assignStaff(10L, req, 1L, "ADMIN"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 400 when updating status of cancelled ticket")
    void updateStatus_CancelledTicket_ThrowsBadRequest() {
        sampleTicket.setStatus(TicketStatus.CANCELLED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.OPEN, "Reopen");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 1L, "ADMIN"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 403 when student updates someone else's ticket")
    void updateStatus_StudentNotOwner_ThrowsForbidden() {
        sampleTicket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.CANCELLED, "Cancel");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 999L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 400 when student cancels ticket that is not OPEN or ASSIGNED")
    void updateStatus_StudentCancelResolved_ThrowsBadRequest() {
        sampleTicket.setStatus(TicketStatus.RESOLVED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.CANCELLED, "Cancel");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 5L, "STUDENT"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 400 when student closes ticket that is not RESOLVED")
    void updateStatus_StudentCloseOpen_ThrowsBadRequest() {
        sampleTicket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.CLOSED, "Close");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 5L, "STUDENT"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 403 when unknown role updates status")
    void updateStatus_UnknownRole_ThrowsForbidden() {
        sampleTicket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        TicketStatusUpdateRequest req = new TicketStatusUpdateRequest(TicketStatus.ASSIGNED, "Assign");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.updateStatus(10L, req, 1L, "GUEST"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 400 when adding comment to cancelled ticket")
    void addComment_CancelledTicket_ThrowsBadRequest() {
        sampleTicket.setStatus(TicketStatus.CANCELLED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        CommentCreateRequest req = new CommentCreateRequest("Note", false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.addComment(10L, req, 5L, "STUDENT"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 403 when unrelated user adds comment")
    void addComment_UnrelatedUser_ThrowsForbidden() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        CommentCreateRequest req = new CommentCreateRequest("Note", false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.addComment(10L, req, 999L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 403 when unrelated user views comments")
    void getComments_UnrelatedUser_ThrowsForbidden() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> ticketService.getComments(10L, 999L, "STUDENT"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
