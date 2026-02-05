package com.phanthanhthien.cmp3025.bookstore.init;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DataInitializer - Khởi tạo dữ liệu mẫu khi ứng dụng start
 *
 * Tự động thêm các danh mục và sách mẫu nếu database trống
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CounterService counterService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Bắt đầu khởi tạo dữ liệu mẫu...");

        // Kiểm tra xem đã có data chưa
        long categoryCount = categoryRepository.count();
        if (categoryCount > 0) {
            logger.info("✅ Database đã có dữ liệu. Bỏ qua khởi tạo.");
            return;
        }

        logger.info("📝 Database trống. Đang thêm dữ liệu mẫu...");

        // Reset counters về 0
        counterService.resetAllCounters();
        logger.info("🔄 Đã reset tất cả counters về 0");

        // 1. Tạo danh mục mẫu
        Category category1 = new Category();
        category1.setName("Văn học");
        category1.setDescription("Sách văn học, tiểu thuyết, thơ ca trong và ngoài nước");
        category1.setCreatedAt(LocalDateTime.now());
        category1.setUpdatedAt(LocalDateTime.now());
        category1 = categoryRepository.save(category1);
        logger.info("✅ Đã tạo danh mục: {}", category1.getName());

        Category category2 = new Category();
        category2.setName("Kinh tế");
        category2.setDescription("Sách kinh tế, quản trị, tài chính, đầu tư");
        category2.setCreatedAt(LocalDateTime.now());
        category2.setUpdatedAt(LocalDateTime.now());
        category2 = categoryRepository.save(category2);
        logger.info("✅ Đã tạo danh mục: {}", category2.getName());

        Category category3 = new Category();
        category3.setName("Kỹ năng");
        category3.setDescription("Sách phát triển bản thân, kỹ năng mềm, kỹ năng chuyên môn");
        category3.setCreatedAt(LocalDateTime.now());
        category3.setUpdatedAt(LocalDateTime.now());
        category3 = categoryRepository.save(category3);
        logger.info("✅ Đã tạo danh mục: {}", category3.getName());

        Category category4 = new Category();
        category4.setName("Khoa học - Công nghệ");
        category4.setDescription("Sách khoa học, công nghệ, lập trình, AI");
        category4.setCreatedAt(LocalDateTime.now());
        category4.setUpdatedAt(LocalDateTime.now());
        category4 = categoryRepository.save(category4);
        logger.info("✅ Đã tạo danh mục: {}", category4.getName());

        Category category5 = new Category();
        category5.setName("Sách thiếu nhi");
        category5.setDescription("Sách truyện, tranh màu, giáo dục cho trẻ em");
        category5.setCreatedAt(LocalDateTime.now());
        category5.setUpdatedAt(LocalDateTime.now());
        category5 = categoryRepository.save(category5);
        logger.info("✅ Đã tạo danh mục: {}", category5.getName());

        // 2. Tạo sách mẫu với hình ảnh
        // Sách Văn học
        createBook("Đắc Nhân Tâm", "Dale Carnegie", "Cuốn sách nổi tiếng về nghệ thuật giao tiếp và ứng xử", new BigDecimal("85000"), 50, category1.getId(),
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop");

        createBook("Nhà Giả Kim", "Paulo Coelho", "Tiểu thuyết về hành trình theo đuổi giấc mơ của chàng chăn cừu Santiago", new BigDecimal("95000"), 30, category1.getId(),
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop");

        createBook("Tù Nhân", "Mikhail Sholokhov", "Tiểu thuyết về cuộc đời của người nông dân Nga trong thời chiến tranh", new BigDecimal("120000"), 25, category1.getId(),
                "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400&h=600&fit=crop");

        // Sách Kinh tế
        createBook("Tư Duy Nhanh Và Chậm", "Daniel Kahneman", "Cuốn sách về hai hệ thống tư duy của con người", new BigDecimal("185000"), 40, category2.getId(),
                "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=600&fit=crop");

        createBook("Từ Tốt Đến Vĩ Đại", "Jim Collins", "Nghiên cứu về cách các công ty vĩ đại được xây dựng", new BigDecimal("145000"), 35, category2.getId(),
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=600&fit=crop");

        createBook("Cha Giàu Cha Nghèo", "Robert Kiyosaki", "Bài học về tiền bạc mà người giàu dạy con cái họ", new BigDecimal("105000"), 60, category2.getId(),
                "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=400&h=600&fit=crop");

        // Sách Kỹ năng
        createBook("7 Thói Quen Để Thành Công", "Stephen Covey", "Sách về phát triển bản thân và quản lý thời gian", new BigDecimal("165000"), 45, category3.getId(),
                "https://images.unsplash.com/photo-1506784983877-45594efa4cbe?w=400&h=600&fit=crop");

        createBook("Quyền Lực Của Thói Quen", "Charles Duhigg", "Sách về cách xây dựng và phá bỏ thói quen", new BigDecimal("155000"), 28, category3.getId(),
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=400&h=600&fit=crop");

        createBook("Đừng Đi Ăn M Một Mình", "Keith Ferrazzi", "Sách về cách xây dựng mạng lưới quan hệ công việc", new BigDecimal("135000"), 32, category3.getId(),
                "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=400&h=600&fit=crop");

        // Sách Khoa học - Công nghệ
        createBook("Vũ Trụ Trong Vỏ Hạt Dẻ", "Stephen Hawking", "Cuốn sách về vũ trụ, không gian và thời gian", new BigDecimal("125000"), 20, category4.getId(),
                "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=400&h=600&fit=crop");

        createBook("Lập Trình Java Bằng Tiếng Việt", "Lê Minh Hoàng", "Sách hướng dẫn lập trình Java cho người mới bắt đầu", new BigDecimal("250000"), 55, category4.getId(),
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=600&fit=crop");

        createBook("Trí Tuệ Nhân Tạo Trong Tương Lai", "Kai-Fu Lee", "Cuốn sách về sự phát triển của AI và tác động đến nhân loại", new BigDecimal("175000"), 38, category4.getId(),
                "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=400&h=600&fit=crop");

        // Sách thiếu nhi
        createBook("Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "Truyện cổ tích về cuộc phiêu lưu của chú dế mèn", new BigDecimal("65000"), 70, category5.getId(),
                "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop");

        createBook("Hoàng Tử Bé", "Truyện cổ tích", "Câu chuyện cổ tích về hoàng tử và công chúa", new BigDecimal("55000"), 80, category5.getId(),
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=400&h=600&fit=crop");

        createBook("Doraemon - Bộ Sưu Tập Truyện Ngắn", "Fujiko F. Fujio", "Bộ truyện ngắn về chú mèo máy Doraemon", new BigDecimal("75000"), 65, category5.getId(),
                "https://images.unsplash.com/photo-1614222455704-70145f483c71?w=400&h=600&fit=crop");

        logger.info("✅ Đã khởi tạo dữ liệu mẫu thành công!");
        logger.info("📊 Tổng số danh mục: {}", categoryRepository.count());
        logger.info("📚 Tổng số sách: {}", bookRepository.count());
    }

    private void createBook(String title, String author, String description, BigDecimal price, int stock, Long categoryId, String imageUrl) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPrice(price);
        book.setStock(stock);
        book.setCategoryId(categoryId);
        book.setImageUrl(imageUrl);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
        logger.info("✅ Đã tạo sách: {} - {}", title, author);
    }
}
