package com.vendora.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import com.vendora.payment.entity.PaymentMethod;

public class PaymentRequest {

    @Schema(hidden = true)
    private Long buyerId;

    @NotNull(message = "Seller ID is required")
    @Schema(description = "User ID of the seller or recipient", example = "4")
    private Long sellerId;

    @Schema(description = "ID of the marketplace listing being purchased (optional)", example = "1")
    private Long listingId;

    @Schema(description = "Type of reference (e.g. LISTING, SCRAP, FEE)", example = "LISTING")
    private String referenceType;

    @Schema(description = "ID of reference entity", example = "1")
    private Long referenceId;

    @Schema(description = "Optional idempotency key to prevent double charging", example = "ORDER-2026-001")
    private String idempotencyKey;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(description = "Payment amount in INR", example = "450.00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "UPI")
    private PaymentMethod paymentMethod;

    @Size(max = 500)
    @Schema(description = "Payment purpose or notes", example = "Payment for Engineering Mechanics Textbook")
    private String description;

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
