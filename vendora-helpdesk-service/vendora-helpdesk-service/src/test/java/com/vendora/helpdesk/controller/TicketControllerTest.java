package com.vendora.helpdesk.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vendora.helpdesk.dto.CommentCreateRequest;
import com.vendora.helpdesk.dto.CommentResponse;
import com.vendora.helpdesk.dto.TicketAssignRequest;
import com.vendora.helpdesk.dto.TicketCreateRequest;
import com.vendora.helpdesk.dto.TicketResponse;
import com.vendora.helpdesk.dto.TicketStatusUpdateRequest;
import com.vendora.helpdesk.entity.TicketCategory;
import com.vendora.helpdesk.entity.TicketPriority;
import com.vendora.helpdesk.entity.TicketStatus;
import com.vendora.helpdesk.service.TicketService;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private TicketResponse sampleTicket;

    @BeforeEach
    void setUp() {
        sampleTicket = new TicketResponse();
        sampleTicket.setId(1L);
        sampleTicket.setTicketNumber("HD-12345678");
        sampleTicket.setUserId(10L);
        sampleTicket.setTitle("Broken lab monitor");
        sampleTicket.setDescription("Monitor flickering constantly");
        sampleTicket.setCategory(TicketCategory.LAB_EQUIPMENT);
        sampleTicket.setPriority(TicketPriority.HIGH);
        sampleTicket.setStatus(TicketStatus.OPEN);
        sampleTicket.setLocation("Lab 301");
        sampleTicket.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createTicket - returns 201 Created")
    void testCreateTicket() {
        TicketCreateRequest req = new TicketCreateRequest("Broken lab monitor", "Monitor flickering constantly",
                TicketCategory.LAB_EQUIPMENT, TicketPriority.HIGH, "Lab 301");

        when(ticketService.createTicket(eq(10L), any(TicketCreateRequest.class))).thenReturn(sampleTicket);

        ResponseEntity<TicketResponse> response = ticketController.createTicket(req, 10L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("HD-12345678", response.getBody().getTicketNumber());
    }

    @Test
    @DisplayName("getMyTickets - returns 200 OK")
    void testGetMyTickets() {
        when(ticketService.getMyTickets(10L)).thenReturn(Collections.singletonList(sampleTicket));

        ResponseEntity<List<TicketResponse>> response = ticketController.getMyTickets(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getAllTickets - returns 200 OK with compound filter")
    void testGetAllTickets() {
        when(ticketService.getAllTickets(TicketStatus.OPEN, TicketCategory.LAB_EQUIPMENT, 5L, "ADMIN"))
                .thenReturn(Collections.singletonList(sampleTicket));

        ResponseEntity<List<TicketResponse>> response = ticketController.getAllTickets(
                TicketStatus.OPEN, TicketCategory.LAB_EQUIPMENT, 5L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getTicketById - returns 200 OK")
    void testGetTicketById() {
        when(ticketService.getTicketById(1L, 10L, "STUDENT")).thenReturn(sampleTicket);

        ResponseEntity<TicketResponse> response = ticketController.getTicketById(1L, 10L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("assignStaff - returns 200 OK")
    void testAssignStaff() {
        TicketAssignRequest assignReq = new TicketAssignRequest(50L);
        sampleTicket.setStatus(TicketStatus.ASSIGNED);
        sampleTicket.setAssignedStaffId(50L);

        when(ticketService.assignStaff(eq(1L), any(TicketAssignRequest.class), eq(99L), eq("ADMIN")))
                .thenReturn(sampleTicket);

        ResponseEntity<TicketResponse> response = ticketController.assignStaff(1L, assignReq, 99L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TicketStatus.ASSIGNED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("updateStatus - returns 200 OK")
    void testUpdateStatus() {
        TicketStatusUpdateRequest updateReq = new TicketStatusUpdateRequest(TicketStatus.RESOLVED, "Fixed monitor cable");
        sampleTicket.setStatus(TicketStatus.RESOLVED);
        sampleTicket.setResolutionNotes("Fixed monitor cable");

        when(ticketService.updateStatus(eq(1L), any(TicketStatusUpdateRequest.class), eq(50L), eq("MAINTENANCE_STAFF")))
                .thenReturn(sampleTicket);

        ResponseEntity<TicketResponse> response = ticketController.updateStatus(1L, updateReq, 50L, "MAINTENANCE_STAFF");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TicketStatus.RESOLVED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("cancelTicket - returns 200 OK")
    void testCancelTicket() {
        sampleTicket.setStatus(TicketStatus.CANCELLED);
        when(ticketService.cancelTicket(1L, 10L, "STUDENT")).thenReturn(sampleTicket);

        ResponseEntity<TicketResponse> response = ticketController.cancelTicket(1L, 10L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TicketStatus.CANCELLED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("addComment - returns 201 Created")
    void testAddComment() {
        CommentCreateRequest commentReq = new CommentCreateRequest("Technician dispatched", false);
        CommentResponse commentResp = new CommentResponse();
        commentResp.setId(100L);
        commentResp.setTicketId(1L);
        commentResp.setMessage("Technician dispatched");

        when(ticketService.addComment(eq(1L), any(CommentCreateRequest.class), eq(50L), eq("MAINTENANCE_STAFF")))
                .thenReturn(commentResp);

        ResponseEntity<CommentResponse> response = ticketController.addComment(1L, commentReq, 50L, "MAINTENANCE_STAFF");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    @DisplayName("getComments - returns 200 OK")
    void testGetComments() {
        CommentResponse commentResp = new CommentResponse();
        commentResp.setId(100L);
        commentResp.setTicketId(1L);
        commentResp.setMessage("Technician dispatched");

        when(ticketService.getComments(1L, 10L, "STUDENT"))
                .thenReturn(Collections.singletonList(commentResp));

        ResponseEntity<List<CommentResponse>> response = ticketController.getComments(1L, 10L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
