package com.phanthanhthien.cmp3025.bookstore.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Đơn hàng sau khi thanh toán
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;

    private String username;

    private List<CartItem> items = new ArrayList<>();

    private BigDecimal totalAmount;

    private String paymentMethod; // MOMO, COD

    private String paymentStatus; // PENDING, SUCCESS, FAILED

    private String momoTransId;

    private String momoRequestId;

    private String orderInfo;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }

    public Order(String userId, String username, List<CartItem> items, BigDecimal totalAmount) {
        this.userId = userId;
        this.username = username;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
        this.paymentStatus = "PENDING";
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getMomoTransId() {
        return momoTransId;
    }

    public void setMomoTransId(String momoTransId) {
        this.momoTransId = momoTransId;
    }

    public String getMomoRequestId() {
        return momoRequestId;
    }

    public void setMomoRequestId(String momoRequestId) {
        this.momoRequestId = momoRequestId;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
