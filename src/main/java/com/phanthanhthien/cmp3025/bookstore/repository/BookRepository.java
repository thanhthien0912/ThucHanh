package com.phanthanhthien.cmp3025.bookstore.repository;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Book Repository - Truy vấn MongoDB cho collection books
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    /**
     * Tìm sách theo tên (không phân biệt hoa thường)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Tìm sách theo tác giả (không phân biệt hoa thường)
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * Tìm sách theo danh mục
     */
    List<Book> findByCategoryId(String categoryId);

    /**
     * Tìm sách có giá nhỏ hơn hoặc bằng
     */
    List<Book> findByPriceLessThanEqual(java.math.BigDecimal price);

    /**
     * Tìm sách theo tên hoặc tác giả
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

}
