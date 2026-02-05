package com.phanthanhthien.cmp3025.bookstore.entities;

import java.math.BigDecimal;

/**
 * CartItem - Item trong giỏ hàng (Embedded Document)
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
public class CartItem {

    private Long bookId;
    private String bookTitle;
    private String author;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Long bookId, String bookTitle, String author, String imageUrl, BigDecimal price,
            Integer quantity) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    // Tính tổng tiền cho item này
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters và Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
