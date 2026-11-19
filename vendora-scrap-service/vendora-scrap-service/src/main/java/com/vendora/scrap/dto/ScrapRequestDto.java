 
package com.vendora.scrap.dto;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import com.vendora.scrap.entity.QuantityUnit;
import com.vendora.scrap.entity.ScrapMaterialType;

public class ScrapRequestDto {

    @Schema(hidden = true)
    private Long userId;
 
    @NotNull(message = "Material type is required")
    private ScrapMaterialType materialType;
 
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
 
    @NotNull(message = "Estimated quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    private BigDecimal estimatedQuantity;
 
    @NotNull(message = "Quantity unit is required")
    private QuantityUnit quantityUnit;
 
    @NotBlank(message = "Pickup location is required")
    @Size(max = 500)
    private String pickupLocation;
 
    private LocalDateTime preferredPickupTime;
 
    public ScrapRequestDto() {
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
}
 