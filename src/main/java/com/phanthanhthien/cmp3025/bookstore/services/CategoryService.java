package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.dto.CategoryDTO;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoryService - Business Logic cho Category
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Lấy tất cả danh mục
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Tìm danh mục theo ID
     */
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Tìm kiếm danh mục
     */
    public List<Category> search(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Lưu danh mục
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Xóa danh mục
     */
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Đếm tổng số danh mục
     */
    public long count() {
        return categoryRepository.count();
    }

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Convert Category entity sang CategoryDTO
     */
    public CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        
        // Đếm số sách trong danh mục
        long bookCount = bookRepository.findByCategoryId(category.getId()).size();
        dto.setBookCount(bookCount);
        
        return dto;
    }

    /**
     * Convert list Category entities sang list CategoryDTOs
     */
    public List<CategoryDTO> convertToDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
