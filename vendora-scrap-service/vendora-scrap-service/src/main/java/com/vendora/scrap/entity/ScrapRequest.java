 
package com.vendora.scrap.entity;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "scrap_requests")
public class ScrapRequest {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private Long userId;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 30)
    private ScrapMaterialType materialType;
 
    @Column(length = 1000)
    private String description;
 
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedQuantity;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_unit", nullable = false, length = 20)
    private QuantityUnit quantityUnit;
 
    @Column(nullable = false, length = 500)
    private String pickupLocation;
 
    private LocalDateTime preferredPickupTime;
 
    private Long assignedStaffId;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ScrapStatus status;
 
    private LocalDateTime createdAt;
 
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
 
        if (status == null) {
            status = ScrapStatus.PENDING;
        }
    }
 
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 
    public ScrapRequest() {
    }
 
    // Getters and Setters
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public ScrapMaterialType getMaterialType() {
        return materialType;
    }
 
    public void setMaterialType(ScrapMaterialType materialType) {
        this.materialType = materialType;
    }
 
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public BigDecimal getEstimatedQuantity() {
        return estimatedQuantity;
    }
 
    public void setEstimatedQuantity(BigDecimal estimatedQuantity) {
        this.estimatedQuantity = estimatedQuantity;
    }
 
    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }
 
    public void setQuantityUnit(QuantityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }
 
    public String getPickupLocation() {
        return pickupLocation;
    }
 
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
 
    public LocalDateTime getPreferredPickupTime() {
        return preferredPickupTime;
    }
 
    public void setPreferredPickupTime(LocalDateTime preferredPickupTime) {
        this.preferredPickupTime = preferredPickupTime;
    }
 
    public Long getAssignedStaffId() {
        return assignedStaffId;
    }
 
    public void setAssignedStaffId(Long assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }
 
    public ScrapStatus getStatus() {
        return status;
    }
 
    public void setStatus(ScrapStatus status) {
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
 