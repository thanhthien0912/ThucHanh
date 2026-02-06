package com.phanthanhthien.cmp3025.bookstore.entities;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Voucher Entity - Mã giảm giá
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Document(collection = "vouchers")
public class Voucher {

    @Id
    private String id;

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 3, max = 50, message = "Mã voucher phải từ 3 đến 50 ký tự")
    private String code;

    private String description;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @DecimalMin(value = "0.01", message = "Phần trăm giảm giá phải lớn hơn 0")
    @DecimalMax(value = "99.99", message = "Phần trăm giảm giá phải nhỏ hơn 100")
    private BigDecimal discountPercent;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minOrderAmount;

    @NotNull(message = "Số lần sử dụng không được để trống")
    @Min(value = 1, message = "Số lần sử dụng phải lớn hơn 0")
    private Integer maxUsage;

    private Integer currentUsage;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime validFrom;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime validTo;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Voucher() {
        this.isActive = true;
        this.currentUsage = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Voucher(String code, String description, BigDecimal discountPercent,
                   BigDecimal maxDiscountAmount, BigDecimal minOrderAmount,
                   Integer maxUsage, LocalDateTime validFrom, LocalDateTime validTo) {
        this.code = code;
        this.description = description;
        this.discountPercent = discountPercent;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.maxUsage = maxUsage;
        this.currentUsage = 0;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Integer getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(Integer maxUsage) {
        this.maxUsage = maxUsage;
    }

    public Integer getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(Integer currentUsage) {
        this.currentUsage = currentUsage;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive &&
                currentUsage < maxUsage &&
                !now.isBefore(validFrom) &&
                !now.isAfter(validTo);
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", discountPercent=" + discountPercent +
                ", maxUsage=" + maxUsage +
                ", currentUsage=" + currentUsage +
                '}';
    }
}
