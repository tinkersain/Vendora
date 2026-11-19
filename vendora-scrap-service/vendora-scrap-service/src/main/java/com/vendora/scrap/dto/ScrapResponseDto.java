package com.vendora.scrap.dto;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import com.vendora.scrap.entity.QuantityUnit;
import com.vendora.scrap.entity.ScrapMaterialType;
import com.vendora.scrap.entity.ScrapStatus;
 
public class ScrapResponseDto {
 
    private Long id;
    private Long userId;
    private ScrapMaterialType materialType;
    private String description;
    private BigDecimal estimatedQuantity;
    private QuantityUnit quantityUnit;
    private String pickupLocation;
    private LocalDateTime preferredPickupTime;
    private Long assignedStaffId;
    private ScrapStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
 
    public ScrapResponseDto() {
    }

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
 