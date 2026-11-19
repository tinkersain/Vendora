package com.vendora.scrap.dto;

import jakarta.validation.constraints.Size;

public class GiveawayInterestDto {

    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;

    public GiveawayInterestDto() {
    }

    public GiveawayInterestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
