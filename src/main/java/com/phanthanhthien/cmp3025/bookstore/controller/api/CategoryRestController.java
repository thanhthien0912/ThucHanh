package com.phanthanhthien.cmp3025.bookstore.controller.api;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryRestController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * GET /api/v1/categories/by-category/{categoryId}/books - Lấy danh sách sách
     * thuộc danh mục
     */
    @GetMapping("/by-category/{categoryId}/books")
    public ResponseEntity<List<Book>> getBooksByCategoryId(@PathVariable Long categoryId) {
        log.info("Fetching books for categoryId: {}", categoryId);
        List<Book> books = bookRepository.findByCategoryId(categoryId);
        log.info("Found {} books for categoryId {}", books.size(), categoryId);
        books.forEach(
                b -> log.info("Book: id={}, title={}, categoryId={}", b.getId(), b.getTitle(), b.getCategoryId()));
        return ResponseEntity.ok(books);
    }

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
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
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
        try {
            // Validate
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }

            // Kiểm tra tên danh mục đã tồn tại chưa
            if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Tên danh mục đã tồn tại");
                return ResponseEntity.badRequest().body(error);
            }

            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo danh mục: " + e.getMessage());
        }
    }

    /**
     * PUT /api/v1/categories/{id} - Cập nhật danh mục
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody Category categoryData) {

        try {
            Optional<Category> existingCategory = categoryRepository.findById(id);
            if (existingCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Validate
            if (categoryData.getName() == null || categoryData.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }

            // Kiểm tra tên mới có trùng với danh mục khác không
            if (!existingCategory.get().getName().equalsIgnoreCase(categoryData.getName())
                    && categoryRepository.existsByNameIgnoreCase(categoryData.getName())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Tên danh mục đã tồn tại");
                return ResponseEntity.badRequest().body(error);
            }

            categoryData.setId(id);
            categoryData.onUpdate();
            Category updatedCategory = categoryRepository.save(categoryData);

            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật danh mục: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/categories/{id} - Xóa danh mục
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);

        Map<String, String> response = new HashMap<>();
        if (category.isPresent()) {
            categoryRepository.deleteById(id);
            response.put("message", "Xóa danh mục thành công");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        }

        response.put("message", "Không tìm thấy danh mục");
        return ResponseEntity.status(404).body(response);
    }

}
