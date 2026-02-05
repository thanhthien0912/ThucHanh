package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.dto.BookDTO;
import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookService - Business Logic cho Book
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Lấy tất cả sách
     */
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * Tìm sách theo ID
     */
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Tìm kiếm sách
     */
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                keyword, keyword);
    }

    /**
     * Lưu sách
     */
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Xóa sách
     */
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Đếm tổng số sách
     */
    public long count() {
        return bookRepository.count();
    }

    /**
     * Convert Book entity sang BookDTO
     */
    public BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setCategoryId(book.getCategoryId());
        dto.setImageUrl(book.getImageUrl());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());

        // Lấy tên danh mục
        if (book.getCategoryId() != null) {
            categoryRepository.findById(book.getCategoryId())
                    .ifPresent(cat -> dto.setCategoryName(cat.getName()));
        }

        return dto;
    }

    /**
     * Convert list Book entities sang list BookDTOs
     */
    public List<BookDTO> convertToDTOList(List<Book> books) {
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
