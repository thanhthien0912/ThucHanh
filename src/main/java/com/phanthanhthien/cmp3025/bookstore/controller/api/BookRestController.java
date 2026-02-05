package com.phanthanhthien.cmp3025.bookstore.controller.api;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BookRestController - REST API cho Quản lý Sách
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookRestController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CounterService counterService;

    /**
     * GET /api/v1/books - Lấy danh sách tất cả sách
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    /**
     * GET /api/v1/books/{id} - Lấy thông tin sách theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/v1/books - Thêm sách mới
     */
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            // Validate và set default values
            if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tiêu đề sách không được để trống");
            }
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tác giả không được để trống");
            }
            if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("Giá sách không hợp lệ");
            }
            if (book.getStock() == null) {
                book.setStock(0);
            }

            // Generate ID using CounterService
            Long newId = counterService.getNextSequence("books");
            book.setId(newId);
            book.setCreatedAt(java.time.LocalDateTime.now());
            book.setUpdatedAt(java.time.LocalDateTime.now());

            Book savedBook = bookRepository.save(book);
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo sách: " + e.getMessage());
        }
    }

    /**
     * PUT /api/v1/books/{id} - Cập nhật sách
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestBody Book bookData) {

        try {
            Optional<Book> existingBook = bookRepository.findById(id);
            if (existingBook.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Validate
            if (bookData.getTitle() == null || bookData.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tiêu đề sách không được để trống");
            }
            if (bookData.getAuthor() == null || bookData.getAuthor().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tác giả không được để trống");
            }
            if (bookData.getPrice() == null || bookData.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body("Giá sách không hợp lệ");
            }

            bookData.setId(id);
            bookData.onUpdate();
            Book updatedBook = bookRepository.save(bookData);

            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật sách: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/books/{id} - Xóa sách
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);

        Map<String, String> response = new HashMap<>();
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            response.put("message", "Xóa sách thành công");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        }

        response.put("message", "Không tìm thấy sách");
        return ResponseEntity.status(404).body(response);
    }

    /**
     * GET /api/v1/books/search?q={keyword} - Tìm kiếm sách
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String q) {
        List<Book> results = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q);
        return ResponseEntity.ok(results);
    }

}
