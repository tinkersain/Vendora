package com.vendora.scrap.dto;
 
import jakarta.validation.constraints.NotNull;
 
public class ScrapAssignmentDto {
 
    @NotNull(message = "Collection staff ID is required")
    private Long assignedStaffId;
 
    public Long getAssignedStaffId() {
        return assignedStaffId;
    }
 
    public void setAssignedStaffId(Long assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }
}
 