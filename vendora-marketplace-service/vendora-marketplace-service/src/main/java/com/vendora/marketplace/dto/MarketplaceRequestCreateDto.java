package com.vendora.marketplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MarketplaceRequestCreateDto {

    @NotNull(message = "Requested quantity is required")
    @Min(value = 1, message = "Requested quantity must be at least 1")
    private Integer requestedQuantity;

    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    public MarketplaceRequestCreateDto() {
    }

    public MarketplaceRequestCreateDto(Integer requestedQuantity, String message) {
        this.requestedQuantity = requestedQuantity;
        this.message = message;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
