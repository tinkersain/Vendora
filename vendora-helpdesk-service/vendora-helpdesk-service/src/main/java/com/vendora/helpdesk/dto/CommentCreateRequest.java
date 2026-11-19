package com.vendora.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateRequest {

    @NotBlank(message = "Comment message cannot be empty")
    @Size(max = 2000, message = "Comment message cannot exceed 2000 characters")
    private String message;

    private Boolean isInternalNote = false;

    public CommentCreateRequest() {
    }

    public CommentCreateRequest(String message, Boolean isInternalNote) {
        this.message = message;
        this.isInternalNote = isInternalNote != null ? isInternalNote : false;
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
}
