package com.phanthanhthien.cmp3025.bookstore.controller.api;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class BookRestController {

    @Autowired
    private BookRepository bookRepository;

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
    public ResponseEntity<?> getBookById(@PathVariable String id) {
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
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    /**
     * PUT /api/v1/books/{id} - Cập nhật sách
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable String id,
            @RequestBody Book bookData) {
        
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        bookData.setId(id);
        bookData.onUpdate();
        Book updatedBook = bookRepository.save(bookData);
        
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * DELETE /api/v1/books/{id} - Xóa sách
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable String id) {
        Optional<Book> book = bookRepository.findById(id);
        
        Map<String, String> response = new HashMap<>();
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            response.put("message", "Xóa sách thành công");
            response.put("id", id);
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
