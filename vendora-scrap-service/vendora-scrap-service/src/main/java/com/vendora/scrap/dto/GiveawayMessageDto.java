package com.vendora.scrap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GiveawayMessageDto {

    @NotBlank(message = "Message text is required")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;

    public GiveawayMessageDto() {
    }

    public GiveawayMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
