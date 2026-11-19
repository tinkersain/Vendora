package com.vendora.user.dto;

import java.time.LocalDateTime;

public class RepresentativeResponse {

    private Long id;
    private Long userId;
    private String approvalStatus;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String reason;
    private LocalDateTime createdAt;

    public RepresentativeResponse() {
    }

    public RepresentativeResponse(Long id, Long userId, String approvalStatus, Long approvedBy,
            LocalDateTime approvedAt, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
