package com.vendora.marketplace.dto;

import java.time.LocalDateTime;

import com.vendora.marketplace.entity.MarketplaceRequestStatus;

public class MarketplaceRequestResponse {

    private Long id;
    private Long listingId;
    private Long buyerId;
    private Integer requestedQuantity;
    private String message;
    private MarketplaceRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MarketplaceRequestResponse() {
    }

    public MarketplaceRequestResponse(Long id, Long listingId, Long buyerId, Integer requestedQuantity,
            String message, MarketplaceRequestStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.listingId = listingId;
        this.buyerId = buyerId;
        this.requestedQuantity = requestedQuantity;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
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

    public MarketplaceRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MarketplaceRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
