package com.vendora.helpdesk.dto;

import com.vendora.helpdesk.entity.TicketStatus;

import jakarta.validation.constraints.NotNull;

public class TicketStatusUpdateRequest {

    @NotNull(message = "New ticket status is required")
    private TicketStatus status;

    private String resolutionNotes;

    public TicketStatusUpdateRequest() {
    }

    public TicketStatusUpdateRequest(TicketStatus status, String resolutionNotes) {
        this.status = status;
        this.resolutionNotes = resolutionNotes;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
