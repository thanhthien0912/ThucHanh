package com.phanthanhthien.cmp3025.bookstore.config;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * DataInitializer - Seed dữ liệu mẫu cho Bookstore
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Configuration
public class DataInitializer {

        private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

        @Bean
        CommandLineRunner initDatabase(CategoryRepository categoryRepository, BookRepository bookRepository) {
                return args -> {
                        // Kiểm tra xem đã có dữ liệu chưa
                        if (categoryRepository.count() > 0 && bookRepository.count() > 0) {
                                logger.info("✅ Database đã có dữ liệu, bỏ qua seeding");
                                return;
                        }

                        logger.info("🚀 Bắt đầu thêm dữ liệu mẫu...");

                        // Xóa dữ liệu cũ (nếu có)
                        bookRepository.deleteAll();
                        categoryRepository.deleteAll();
                        logger.info("🗑️ Đã xóa dữ liệu cũ");

                        // THÊM DANH MỤC
                        Category[] categories = {
                                        new Category("Văn học Việt Nam",
                                                        "Các tác phẩm văn học của các tác giả Việt Nam"),
                                        new Category("Văn học nước ngoài", "Các tác phẩm văn học dịch từ nước ngoài"),
                                        new Category("Kinh tế - Kinh doanh",
                                                        "Sách về kinh tế, tài chính, quản trị kinh doanh"),
                                        new Category("Kỹ năng sống", "Sách phát triển bản thân, kỹ năng mềm"),
                                        new Category("Khoa học - Công nghệ", "Sách về khoa học tự nhiên và công nghệ"),
                                        new Category("Lập trình - CNTT", "Sách về lập trình, công nghệ thông tin"),
                                        new Category("Thiếu nhi", "Sách dành cho trẻ em"),
                                        new Category("Tâm lý - Triết học", "Sách về tâm lý học và triết học"),
                                        new Category("Lịch sử", "Sách về lịch sử Việt Nam và thế giới"),
                                        new Category("Truyện tranh", "Manga, comic và truyện tranh")
                        };

                        categoryRepository.saveAll(Arrays.asList(categories));
                        logger.info("📁 Đã thêm {} danh mục", categories.length);

                        // Lấy ID các danh mục vào map
                        Map<String, String> catMap = new HashMap<>();
                        categoryRepository.findAll().forEach(c -> catMap.put(c.getName(), c.getId()));

                        // THÊM SÁCH với imageUrl
                        Book[] books = {
                                        // Văn học Việt Nam
                                        createBook("Số Đỏ", "Vũ Trọng Phụng", "Tiểu thuyết trào phúng nổi tiếng",
                                                        85000, 50, catMap.get("Văn học Việt Nam"),
                                                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=300&h=400&fit=crop"),
                                        createBook("Tắt Đèn", "Ngô Tất Tố", "Tác phẩm về đời sống nông dân Việt Nam",
                                                        75000, 45, catMap.get("Văn học Việt Nam"),
                                                        "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=300&h=400&fit=crop"),
                                        createBook("Dế Mèn Phiêu Lưu Ký", "Tô Hoài",
                                                        "Câu chuyện phiêu lưu của chú Dế Mèn",
                                                        65000, 80, catMap.get("Văn học Việt Nam"),
                                                        "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=300&h=400&fit=crop"),
                                        createBook("Truyện Kiều", "Nguyễn Du", "Kiệt tác văn học cổ điển Việt Nam",
                                                        120000, 35, catMap.get("Văn học Việt Nam"),
                                                        "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=300&h=400&fit=crop"),

                                        // Văn học nước ngoài
                                        createBook("Đắc Nhân Tâm", "Dale Carnegie", "Nghệ thuật giao tiếp và ứng xử",
                                                        108000, 200, catMap.get("Văn học nước ngoài"),
                                                        "https://images.unsplash.com/photo-1589998059171-988d887df646?w=300&h=400&fit=crop"),
                                        createBook("Nhà Giả Kim", "Paulo Coelho", "Hành trình theo đuổi giấc mơ",
                                                        79000, 150, catMap.get("Văn học nước ngoài"),
                                                        "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=300&h=400&fit=crop"),
                                        createBook("1984", "George Orwell", "Tiểu thuyết dystopia kinh điển",
                                                        135000, 60, catMap.get("Văn học nước ngoài"),
                                                        "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=300&h=400&fit=crop"),
                                        createBook("Harry Potter", "J.K. Rowling", "Cuộc phiêu lưu của Harry Potter",
                                                        185000, 100, catMap.get("Văn học nước ngoài"),
                                                        "https://images.unsplash.com/photo-1618666012174-83b441c0bc76?w=300&h=400&fit=crop"),

                                        // Kinh tế - Kinh doanh
                                        createBook("Cha Giàu Cha Nghèo", "Robert Kiyosaki",
                                                        "Bài học về tiền bạc và đầu tư",
                                                        125000, 120, catMap.get("Kinh tế - Kinh doanh"),
                                                        "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=300&h=400&fit=crop"),
                                        createBook("Tư Duy Nhanh Và Chậm", "Daniel Kahneman", "Nghiên cứu về tư duy",
                                                        245000, 40, catMap.get("Kinh tế - Kinh doanh"),
                                                        "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=300&h=400&fit=crop"),
                                        createBook("Khởi Nghiệp Tinh Gọn", "Eric Ries", "Phương pháp xây dựng startup",
                                                        189000, 55, catMap.get("Kinh tế - Kinh doanh"),
                                                        "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=300&h=400&fit=crop"),

                                        // Kỹ năng sống
                                        createBook("7 Thói Quen Hiệu Quả", "Stephen Covey", "7 thói quen thành công",
                                                        165000, 85, catMap.get("Kỹ năng sống"),
                                                        "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=300&h=400&fit=crop"),
                                        createBook("Sức Mạnh Của Thói Quen", "Charles Duhigg", "Khoa học về thói quen",
                                                        145000, 70, catMap.get("Kỹ năng sống"),
                                                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=300&h=400&fit=crop"),
                                        createBook("Đời Ngắn Đừng Ngủ Dài", "Robin Sharma", "Bài học về cuộc sống",
                                                        95000, 90, catMap.get("Kỹ năng sống"),
                                                        "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=300&h=400&fit=crop"),

                                        // Lập trình - CNTT
                                        createBook("Clean Code", "Robert C. Martin", "Nghệ thuật viết code sạch",
                                                        450000, 30, catMap.get("Lập trình - CNTT"),
                                                        "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=300&h=400&fit=crop"),
                                        createBook("Head First Java", "Kathy Sierra", "Học lập trình Java",
                                                        380000, 45, catMap.get("Lập trình - CNTT"),
                                                        "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=300&h=400&fit=crop"),
                                        createBook("Spring in Action", "Craig Walls", "Hướng dẫn Spring Framework",
                                                        520000, 25, catMap.get("Lập trình - CNTT"),
                                                        "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=300&h=400&fit=crop"),
                                        createBook("JavaScript Guide", "Douglas Crockford",
                                                        "Những phần hay của JavaScript",
                                                        290000, 40, catMap.get("Lập trình - CNTT"),
                                                        "https://images.unsplash.com/photo-1579468118864-1b9ea3c0db4a?w=300&h=400&fit=crop"),

                                        // Khoa học - Công nghệ
                                        createBook("Lược Sử Thời Gian", "Stephen Hawking", "Giải thích về vũ trụ",
                                                        155000, 65, catMap.get("Khoa học - Công nghệ"),
                                                        "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=300&h=400&fit=crop"),
                                        createBook("Sapiens", "Yuval Noah Harari", "Lịch sử loài người",
                                                        195000, 75, catMap.get("Khoa học - Công nghệ"),
                                                        "https://images.unsplash.com/photo-1507413245164-6160d8298b31?w=300&h=400&fit=crop"),

                                        // Thiếu nhi
                                        createBook("Doraemon Tập 1", "Fujiko F. Fujio", "Chú mèo máy từ tương lai",
                                                        25000, 200, catMap.get("Thiếu nhi"),
                                                        "https://images.unsplash.com/photo-1629992101753-56d196c8aabb?w=300&h=400&fit=crop"),
                                        createBook("Hoàng Tử Bé", "Antoine de Saint-Exupéry", "Câu chuyện triết lý",
                                                        85000, 120, catMap.get("Thiếu nhi"),
                                                        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=300&h=400&fit=crop"),

                                        // Tâm lý - Triết học
                                        createBook("Đếch Quan Tâm", "Mark Manson", "Nghệ thuật sống ý nghĩa",
                                                        139000, 95, catMap.get("Tâm lý - Triết học"),
                                                        "https://images.unsplash.com/photo-1474631245212-32dc3c8310c6?w=300&h=400&fit=crop"),
                                        createBook("Bạn Đắt Giá Bao Nhiêu", "Vãn Tình", "Sách về giá trị bản thân",
                                                        89000, 110, catMap.get("Tâm lý - Triết học"),
                                                        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=300&h=400&fit=crop"),

                                        // Lịch sử
                                        createBook("Đại Việt Sử Ký", "Ngô Sĩ Liên", "Bộ quốc sử Việt Nam",
                                                        350000, 20, catMap.get("Lịch sử"),
                                                        "https://images.unsplash.com/photo-1461360370896-922624d12a74?w=300&h=400&fit=crop"),
                                        createBook("Điện Biên Phủ", "Võ Nguyên Giáp", "Hồi ức chiến thắng lịch sử",
                                                        185000, 35, catMap.get("Lịch sử"),
                                                        "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=300&h=400&fit=crop"),

                                        // Truyện tranh
                                        createBook("One Piece Tập 1", "Eiichiro Oda", "Hành trình tìm kho báu",
                                                        30000, 150, catMap.get("Truyện tranh"),
                                                        "https://images.unsplash.com/photo-1608889175123-8ee362201f81?w=300&h=400&fit=crop"),
                                        createBook("Naruto Tập 1", "Masashi Kishimoto", "Câu chuyện ninja Naruto",
                                                        30000, 130, catMap.get("Truyện tranh"),
                                                        "https://images.unsplash.com/photo-1613376023733-0a73315d9b06?w=300&h=400&fit=crop"),
                                        createBook("Dragon Ball Tập 1", "Akira Toriyama", "Phiêu lưu của Goku",
                                                        28000, 100, catMap.get("Truyện tranh"),
                                                        "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=300&h=400&fit=crop")
                        };

                        bookRepository.saveAll(Arrays.asList(books));
                        logger.info("📚 Đã thêm {} sách", books.length);

                        // HIỂN THỊ KẾT QUẢ
                        logger.info("");
                        logger.info("✅ HOÀN THÀNH!");
                        logger.info("📁 Tổng danh mục: {}", categoryRepository.count());
                        logger.info("📖 Tổng sách: {}", bookRepository.count());
                };
        }

        /**
         * Helper method để tạo Book với imageUrl
         */
        private Book createBook(String title, String author, String description,
                        int price, int stock, String categoryId, String imageUrl) {
                Book book = new Book(title, author, description, new BigDecimal(price), stock, categoryId);
                book.setImageUrl(imageUrl);
                return book;
        }
}
