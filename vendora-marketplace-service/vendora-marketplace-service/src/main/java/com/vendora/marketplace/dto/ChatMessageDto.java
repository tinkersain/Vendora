package com.vendora.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatMessageDto {

    @NotBlank(message = "Message text is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    public ChatMessageDto() {
    }

    public ChatMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
