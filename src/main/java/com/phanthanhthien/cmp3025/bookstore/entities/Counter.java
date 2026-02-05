package com.phanthanhthien.cmp3025.bookstore.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Counter Entity - Quản lý Auto Increment ID
 * 
 * Lưu trữ sequence số cho mỗi collection:
 * - _id: Tên collection (ví dụ: "books", "categories")
 * - seq: Giá trị sequence hiện tại
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Document(collection = "counters")
public class Counter {

    @Id
    private String id;

    private Long seq;

    public Counter() {
    }

    public Counter(String id, Long seq) {
        this.id = id;
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
}
