package com.phanthanhthien.cmp3025.bookstore.controller.api;

import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CategoryRestController - REST API cho Quản lý Danh mục
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * GET /api/v1/categories - Lấy danh sách tất cả danh mục
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    /**
     * GET /api/v1/categories/{id} - Lấy thông tin danh mục theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/v1/categories - Thêm danh mục mới
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        // Kiểm tra tên danh mục đã tồn tại chưa
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Tên danh mục đã tồn tại");
            return ResponseEntity.badRequest().body(error);
        }
        
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    /**
     * PUT /api/v1/categories/{id} - Cập nhật danh mục
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable String id,
            @RequestBody Category categoryData) {
        
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        categoryData.setId(id);
        categoryData.onUpdate();
        Category updatedCategory = categoryRepository.save(categoryData);
        
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * DELETE /api/v1/categories/{id} - Xóa danh mục
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable String id) {
        Optional<Category> category = categoryRepository.findById(id);
        
        Map<String, String> response = new HashMap<>();
        if (category.isPresent()) {
            categoryRepository.deleteById(id);
            response.put("message", "Xóa danh mục thành công");
            response.put("id", id);
            return ResponseEntity.ok(response);
        }
        
        response.put("message", "Không tìm thấy danh mục");
        return ResponseEntity.status(404).body(response);
    }

}
