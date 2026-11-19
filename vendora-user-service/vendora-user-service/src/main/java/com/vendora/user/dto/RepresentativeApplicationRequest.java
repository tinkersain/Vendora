package com.vendora.user.dto;

import jakarta.validation.constraints.Size;

public class RepresentativeApplicationRequest {

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    public RepresentativeApplicationRequest() {
    }

    public RepresentativeApplicationRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
