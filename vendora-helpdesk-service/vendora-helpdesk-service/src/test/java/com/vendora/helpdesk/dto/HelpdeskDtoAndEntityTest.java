package com.vendora.helpdesk.dto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vendora.helpdesk.entity.Ticket;
import com.vendora.helpdesk.entity.TicketCategory;
import com.vendora.helpdesk.entity.TicketComment;
import com.vendora.helpdesk.entity.TicketPriority;
import com.vendora.helpdesk.entity.TicketStatus;

class HelpdeskDtoAndEntityTest {

    @Test
    @DisplayName("Ticket entity and DTO tests")
    void testTicketEntityAndDtos() {
        LocalDateTime now = LocalDateTime.now();
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketNumber("HD-ABC123");
        ticket.setUserId(10L);
        ticket.setTitle("Water leakage");
        ticket.setDescription("Tap dripping in washroom");
        ticket.setCategory(TicketCategory.PLUMBING);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setLocation("Hostel 2, 1st Floor");
        ticket.setAssignedStaffId(5L);
        ticket.setResolutionNotes("Fixed washer");
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);
        ticket.setResolvedAt(now);

        assertEquals(1L, ticket.getId());
        assertEquals("HD-ABC123", ticket.getTicketNumber());
        assertEquals(10L, ticket.getUserId());
        assertEquals("Water leakage", ticket.getTitle());
        assertEquals("Tap dripping in washroom", ticket.getDescription());
        assertEquals(TicketCategory.PLUMBING, ticket.getCategory());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals("Hostel 2, 1st Floor", ticket.getLocation());
        assertEquals(5L, ticket.getAssignedStaffId());
        assertEquals("Fixed washer", ticket.getResolutionNotes());
        assertEquals(now, ticket.getCreatedAt());
        assertEquals(now, ticket.getUpdatedAt());
        assertEquals(now, ticket.getResolvedAt());

        // Test TicketCreateRequest full getters, setters, and constructors
        TicketCreateRequest createReq = new TicketCreateRequest(
                "Water leakage", "Tap dripping", TicketCategory.PLUMBING, TicketPriority.MEDIUM, "Hostel 2"
        );
        assertEquals("Water leakage", createReq.getTitle());
        assertEquals("Tap dripping", createReq.getDescription());
        assertEquals(TicketCategory.PLUMBING, createReq.getCategory());
        assertEquals(TicketPriority.MEDIUM, createReq.getPriority());
        assertEquals("Hostel 2", createReq.getLocation());

        TicketCreateRequest emptyCreateReq = new TicketCreateRequest();
        emptyCreateReq.setTitle("Electrical Spark");
        emptyCreateReq.setDescription("Spark in switchboard");
        emptyCreateReq.setCategory(TicketCategory.ELECTRICAL);
        emptyCreateReq.setPriority(TicketPriority.HIGH);
        emptyCreateReq.setLocation("Lab 4");

        assertEquals("Electrical Spark", emptyCreateReq.getTitle());
        assertEquals("Spark in switchboard", emptyCreateReq.getDescription());
        assertEquals(TicketCategory.ELECTRICAL, emptyCreateReq.getCategory());
        assertEquals(TicketPriority.HIGH, emptyCreateReq.getPriority());
        assertEquals("Lab 4", emptyCreateReq.getLocation());

        // Default priority constructor
        TicketCreateRequest defaultPriReq = new TicketCreateRequest("Title", "Desc", TicketCategory.OTHER, null, "Loc");
        assertEquals(TicketPriority.MEDIUM, defaultPriReq.getPriority());

        TicketResponse resp = new TicketResponse();
        resp.setId(1L);
        resp.setTicketNumber("HD-ABC123");
        resp.setUserId(10L);
        resp.setTitle("Water leakage");
        resp.setDescription("Tap dripping");
        resp.setCategory(TicketCategory.PLUMBING);
        resp.setPriority(TicketPriority.MEDIUM);
        resp.setStatus(TicketStatus.OPEN);
        resp.setLocation("Hostel 2");
        resp.setAssignedStaffId(5L);
        resp.setResolutionNotes("Resolved");
        resp.setCreatedAt(now);
        resp.setUpdatedAt(now);
        resp.setResolvedAt(now);
        resp.setCommentCount(3);

        assertEquals(1L, resp.getId());
        assertEquals(3, resp.getCommentCount());

        // TicketAssignRequest constructors
        TicketAssignRequest assignReq = new TicketAssignRequest(5L);
        assertEquals(5L, assignReq.getStaffId());
        assignReq.setStaffId(6L);
        assertEquals(6L, assignReq.getStaffId());

        TicketAssignRequest emptyAssignReq = new TicketAssignRequest();
        emptyAssignReq.setStaffId(7L);
        assertEquals(7L, emptyAssignReq.getStaffId());

        // TicketStatusUpdateRequest constructors
        TicketStatusUpdateRequest statusReq = new TicketStatusUpdateRequest(TicketStatus.RESOLVED, "Fixed");
        assertEquals(TicketStatus.RESOLVED, statusReq.getStatus());
        assertEquals("Fixed", statusReq.getResolutionNotes());
        statusReq.setStatus(TicketStatus.CLOSED);
        statusReq.setResolutionNotes("Closed");
        assertEquals(TicketStatus.CLOSED, statusReq.getStatus());
        assertEquals("Closed", statusReq.getResolutionNotes());

        TicketStatusUpdateRequest emptyStatusReq = new TicketStatusUpdateRequest();
        emptyStatusReq.setStatus(TicketStatus.IN_PROGRESS);
        emptyStatusReq.setResolutionNotes("Working on it");
        assertEquals(TicketStatus.IN_PROGRESS, emptyStatusReq.getStatus());
        assertEquals("Working on it", emptyStatusReq.getResolutionNotes());
    }

    @Test
    @DisplayName("Ticket entity lifecycle callbacks")
    void testTicketLifecycle() throws Exception {
        Ticket ticket = new Ticket();
        // Invoke onCreate via reflection
        java.lang.reflect.Method onCreate = Ticket.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(ticket);

        assertNotNull(ticket.getCreatedAt());
        assertNotNull(ticket.getUpdatedAt());
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());

        // Call onUpdate
        java.lang.reflect.Method onUpdate = Ticket.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(ticket);
        assertNotNull(ticket.getUpdatedAt());
    }

    @Test
    @DisplayName("TicketComment entity and DTO tests")
    void testCommentEntityAndDtos() {
        LocalDateTime now = LocalDateTime.now();
        TicketComment comment = new TicketComment();
        comment.setId(1L);
        comment.setTicketId(10L);
        comment.setSenderId(5L);
        comment.setSenderRole("MAINTENANCE_STAFF");
        comment.setMessage("Parts ordered");
        comment.setIsInternalNote(true);
        comment.setCreatedAt(now);

        assertEquals(1L, comment.getId());
        assertEquals(10L, comment.getTicketId());
        assertEquals(5L, comment.getSenderId());
        assertEquals("MAINTENANCE_STAFF", comment.getSenderRole());
        assertEquals("Parts ordered", comment.getMessage());
        assertTrue(comment.getIsInternalNote());
        assertEquals(now, comment.getCreatedAt());

        // Test all-args comment constructor
        TicketComment comment2 = new TicketComment(10L, 5L, "MAINTENANCE_STAFF", "Parts ordered", true);
        assertEquals(10L, comment2.getTicketId());
        assertEquals("Parts ordered", comment2.getMessage());

        CommentCreateRequest createReq = new CommentCreateRequest("Parts ordered", true);
        assertEquals("Parts ordered", createReq.getMessage());
        assertTrue(createReq.getIsInternalNote());
        createReq.setMessage("Updated");
        createReq.setIsInternalNote(false);
        assertEquals("Updated", createReq.getMessage());
        assertFalse(createReq.getIsInternalNote());

        CommentCreateRequest emptyCommentReq = new CommentCreateRequest();
        emptyCommentReq.setMessage("Empty comment");
        emptyCommentReq.setIsInternalNote(true);
        assertEquals("Empty comment", emptyCommentReq.getMessage());
        assertTrue(emptyCommentReq.getIsInternalNote());

        CommentResponse resp = new CommentResponse();
        resp.setId(1L);
        resp.setTicketId(10L);
        resp.setSenderId(5L);
        resp.setSenderRole("MAINTENANCE_STAFF");
        resp.setMessage("Parts ordered");
        resp.setIsInternalNote(true);
        resp.setCreatedAt(now);

        assertEquals(1L, resp.getId());
        assertEquals(10L, resp.getTicketId());
        assertEquals("MAINTENANCE_STAFF", resp.getSenderRole());
    }

    @Test
    @DisplayName("Enums coverage")
    void testEnums() {
        assertEquals(TicketCategory.ELECTRICAL, TicketCategory.valueOf("ELECTRICAL"));
        assertEquals(TicketCategory.PLUMBING, TicketCategory.valueOf("PLUMBING"));
        assertEquals(TicketCategory.FURNITURE, TicketCategory.valueOf("FURNITURE"));
        assertEquals(TicketCategory.INTERNET_WIFI, TicketCategory.valueOf("INTERNET_WIFI"));
        assertEquals(TicketCategory.LAB_EQUIPMENT, TicketCategory.valueOf("LAB_EQUIPMENT"));
        assertEquals(TicketCategory.SANITATION, TicketCategory.valueOf("SANITATION"));
        assertEquals(TicketCategory.AC_COOLING, TicketCategory.valueOf("AC_COOLING"));
        assertEquals(TicketCategory.OTHER, TicketCategory.valueOf("OTHER"));

        assertEquals(TicketPriority.LOW, TicketPriority.valueOf("LOW"));
        assertEquals(TicketPriority.MEDIUM, TicketPriority.valueOf("MEDIUM"));
        assertEquals(TicketPriority.HIGH, TicketPriority.valueOf("HIGH"));
        assertEquals(TicketPriority.URGENT, TicketPriority.valueOf("URGENT"));

        assertEquals(TicketStatus.OPEN, TicketStatus.valueOf("OPEN"));
        assertEquals(TicketStatus.ASSIGNED, TicketStatus.valueOf("ASSIGNED"));
        assertEquals(TicketStatus.IN_PROGRESS, TicketStatus.valueOf("IN_PROGRESS"));
        assertEquals(TicketStatus.RESOLVED, TicketStatus.valueOf("RESOLVED"));
        assertEquals(TicketStatus.CLOSED, TicketStatus.valueOf("CLOSED"));
        assertEquals(TicketStatus.CANCELLED, TicketStatus.valueOf("CANCELLED"));
    }
}
