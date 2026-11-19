package com.vendora.scrap.dto;

import java.time.LocalDateTime;

public class GiveawayInterestResponse {

    private Long id;
    private Long giveawayId;
    private Long userId;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    public GiveawayInterestResponse() {
    }

    public GiveawayInterestResponse(Long id, Long giveawayId, Long userId, String message, String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.giveawayId = giveawayId;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGiveawayId() {
        return giveawayId;
    }

    public void setGiveawayId(Long giveawayId) {
        this.giveawayId = giveawayId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
