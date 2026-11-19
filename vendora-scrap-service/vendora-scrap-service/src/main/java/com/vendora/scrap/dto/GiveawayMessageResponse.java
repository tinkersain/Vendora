package com.vendora.scrap.dto;

import java.time.LocalDateTime;

public class GiveawayMessageResponse {

    private Long id;
    private Long giveawayId;
    private Long senderId;
    private String message;
    private LocalDateTime createdAt;

    public GiveawayMessageResponse() {
    }

    public GiveawayMessageResponse(Long id, Long giveawayId, Long senderId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.giveawayId = giveawayId;
        this.senderId = senderId;
        this.message = message;
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

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
