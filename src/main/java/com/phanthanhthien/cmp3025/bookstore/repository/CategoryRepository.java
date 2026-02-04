package com.phanthanhthien.cmp3025.bookstore.repository;

import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category Repository - Truy vấn MongoDB cho collection categories
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    /**
     * Tìm danh mục theo tên (không phân biệt hoa thường)
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     */
    boolean existsByNameIgnoreCase(String name);

}
