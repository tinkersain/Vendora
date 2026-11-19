package com.vendora.helpdesk.dto;

import jakarta.validation.constraints.NotNull;

public class TicketAssignRequest {

    @NotNull(message = "Staff user ID is required")
    private Long staffId;

    public TicketAssignRequest() {
    }

    public TicketAssignRequest(Long staffId) {
        this.staffId = staffId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
