package com.vendora.helpdesk.dto;

import java.time.LocalDateTime;

import com.vendora.helpdesk.entity.TicketComment;

public class CommentResponse {

    private Long id;
    private Long ticketId;
    private Long senderId;
    private String senderRole;
    private String message;
    private Boolean isInternalNote;
    private LocalDateTime createdAt;

    public CommentResponse() {
    }

    public static CommentResponse fromEntity(TicketComment comment) {
        CommentResponse res = new CommentResponse();
        res.setId(comment.getId());
        res.setTicketId(comment.getTicketId());
        res.setSenderId(comment.getSenderId());
        res.setSenderRole(comment.getSenderRole());
        res.setMessage(comment.getMessage());
        res.setIsInternalNote(comment.getIsInternalNote());
        res.setCreatedAt(comment.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsInternalNote() {
        return isInternalNote;
    }

    public void setIsInternalNote(Boolean isInternalNote) {
        this.isInternalNote = isInternalNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
