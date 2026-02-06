package com.phanthanhthien.cmp3025.bookstore.init;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.VoucherRepository;
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
    private VoucherRepository voucherRepository;

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

        // 1. Tạo danh mục mẫu với ID tự động tăng
        createCategory("Văn học", "Sách văn học, tiểu thuyết, thơ ca trong và ngoài nước");
        createCategory("Kinh tế", "Sách kinh tế, quản trị, tài chính, đầu tư");
        createCategory("Kỹ năng", "Sách phát triển bản thân, kỹ năng mềm, kỹ năng chuyên môn");
        createCategory("Khoa học - Công nghệ", "Sách khoa học, công nghệ, lập trình, AI");
        createCategory("Sách thiếu nhi", "Sách truyện, tranh màu, giáo dục cho trẻ em");
        createCategory("Văn học Việt Nam", "Các tác phẩm văn học của các tác giả Việt Nam");
        createCategory("Văn học nước ngoài", "Các tác phẩm văn học dịch từ nước ngoài");
        createCategory("Lịch sử", "Sách về lịch sử Việt Nam và thế giới");
        createCategory("Tâm lý - Triết học", "Sách về tâm lý học và triết học");
        createCategory("Truyện tranh", "Manga, comic và truyện tranh");

        // Lấy danh sách categories để map tên -> ID
        var categories = categoryRepository.findAll();
        var categoryMap = categories.stream()
                .collect(java.util.stream.Collectors.toMap(
                        Category::getName,
                        Category::getId
                ));

        // 2. Tạo sách mẫu với hình ảnh

        // Văn học
        Long catVanHoc = categoryMap.get("Văn học");
        createBook("Đắc Nhân Tâm", "Dale Carnegie", "Cuốn sách nổi tiếng về nghệ thuật giao tiếp và ứng xử",
                new BigDecimal("85000"), 50, catVanHoc,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=600&fit=crop");
        createBook("Nhà Giả Kim", "Paulo Coelho", "Tiểu thuyết về hành trình theo đuổi giấc mơ của chàng chăn cừu Santiago",
                new BigDecimal("95000"), 30, catVanHoc,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400&h=600&fit=crop");
        createBook("Số Đỏ", "Vũ Trọng Phụng", "Tiểu thuyết trào phúng nổi tiếng",
                new BigDecimal("85000"), 50, catVanHoc,
                "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400&h=600&fit=crop");
        createBook("Tắt Đèn", "Ngô Tất Tố", "Tác phẩm về đời sống nông dân Việt Nam",
                new BigDecimal("75000"), 45, catVanHoc,
                "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400&h=600&fit=crop");
        createBook("Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "Câu chuyện phiêu lưu của chú Dế Mèn",
                new BigDecimal("65000"), 80, catVanHoc,
                "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400&h=600&fit=crop");
        createBook("Truyện Kiều", "Nguyễn Du", "Kiệt tác văn học cổ điển Việt Nam",
                new BigDecimal("120000"), 35, catVanHoc,
                "https://images.unsplash.com/photo-1476275466078-4007374efbbe?w=400&h=600&fit=crop");
        createBook("1984", "George Orwell", "Tiểu thuyết dystopia kinh điển",
                new BigDecimal("135000"), 60, catVanHoc,
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=400&h=600&fit=crop");
        createBook("Harry Potter", "J.K. Rowling", "Cuộc phiêu lưu của Harry Potter",
                new BigDecimal("185000"), 100, catVanHoc,
                "https://images.unsplash.com/photo-1618666012174-83b441c0bc76?w=400&h=600&fit=crop");

        // Kinh tế
        Long catKinhTe = categoryMap.get("Kinh tế");
        createBook("Tư Duy Nhanh Và Chậm", "Daniel Kahneman", "Cuốn sách về hai hệ thống tư duy của con người",
                new BigDecimal("185000"), 40, catKinhTe,
                "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=600&fit=crop");
        createBook("Từ Tốt Đến Vĩ Đại", "Jim Collins", "Nghiên cứu về cách các công ty vĩ đại được xây dựng",
                new BigDecimal("145000"), 35, catKinhTe,
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=600&fit=crop");
        createBook("Cha Giàu Cha Nghèo", "Robert Kiyosaki", "Bài học về tiền bạc mà người giàu dạy con cái họ",
                new BigDecimal("105000"), 60, catKinhTe,
                "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=400&h=600&fit=crop");
        createBook("Khởi Nghiệp Tinh Gọn", "Eric Ries", "Phương pháp xây dựng startup",
                new BigDecimal("189000"), 55, catKinhTe,
                "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=400&h=600&fit=crop");

        // Kỹ năng
        Long catKyNang = categoryMap.get("Kỹ năng");
        createBook("7 Thói Quen Để Thành Công", "Stephen Covey", "Sách về phát triển bản thân và quản lý thời gian",
                new BigDecimal("165000"), 45, catKyNang,
                "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400&h=600&fit=crop");
        createBook("Quyền Lực Của Thói Quen", "Charles Duhigg", "Sách về cách xây dựng và phá bỏ thói quen",
                new BigDecimal("155000"), 28, catKyNang,
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=400&h=600&fit=crop");
        createBook("Đừng Đi Ăn Một Mình", "Keith Ferrazzi", "Sách về cách xây dựng mạng lưới quan hệ công việc",
                new BigDecimal("135000"), 32, catKyNang,
                "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=400&h=600&fit=crop");
        createBook("Đời Ngắn Đừng Ngủ Dài", "Robin Sharma", "Bài học về cuộc sống",
                new BigDecimal("95000"), 90, catKyNang,
                "https://images.unsplash.com/photo-1519682337058-a94d519337bc?w=400&h=600&fit=crop");

        // Khoa học - Công nghệ
        Long catKhoaHoc = categoryMap.get("Khoa học - Công nghệ");
        createBook("Vũ Trụ Trong Vỏ Hạt Dẻ", "Stephen Hawking", "Cuốn sách về vũ trụ, không gian và thời gian",
                new BigDecimal("125000"), 20, catKhoaHoc,
                "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=400&h=600&fit=crop");
        createBook("Lập Trình Java Bằng Tiếng Việt", "Lê Minh Hoàng", "Sách hướng dẫn lập trình Java cho người mới bắt đầu",
                new BigDecimal("250000"), 55, catKhoaHoc,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=600&fit=crop");
        createBook("Trí Tuệ Nhân Tạo Trong Tương Lai", "Kai-Fu Lee", "Cuốn sách về sự phát triển của AI và tác động đến nhân loại",
                new BigDecimal("175000"), 38, catKhoaHoc,
                "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=400&h=600&fit=crop");
        createBook("Clean Code", "Robert C. Martin", "Nghệ thuật viết code sạch",
                new BigDecimal("450000"), 30, catKhoaHoc,
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=400&h=600&fit=crop");
        createBook("Head First Java", "Kathy Sierra", "Học lập trình Java",
                new BigDecimal("380000"), 45, catKhoaHoc,
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&h=600&fit=crop");
        createBook("Spring in Action", "Craig Walls", "Hướng dẫn Spring Framework",
                new BigDecimal("520000"), 25, catKhoaHoc,
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400&h=600&fit=crop");

        // Sách thiếu nhi
        Long catThieuNhi = categoryMap.get("Sách thiếu nhi");
        createBook("Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "Truyện cổ tích về cuộc phiêu lưu của chú dế mèn",
                new BigDecimal("65000"), 70, catThieuNhi,
                "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=600&fit=crop");
        createBook("Hoàng Tử Bé", "Truyện cổ tích", "Câu chuyện cổ tích về hoàng tử và công chúa",
                new BigDecimal("55000"), 80, catThieuNhi,
                "https://images.unsplash.com/photo-1541963463532-d68292c34b19?w=400&h=600&fit=crop");
        createBook("Doraemon - Bộ Sưu Tập Truyện Ngắn", "Fujiko F. Fujio", "Bộ truyện ngắn về chú mèo máy Doraemon",
                new BigDecimal("75000"), 65, catThieuNhi,
                "https://images.unsplash.com/photo-1614222455704-70145f483c71?w=400&h=600&fit=crop");
        createBook("Doraemon Tập 1", "Fujiko F. Fujio", "Chú mèo máy từ tương lai",
                new BigDecimal("25000"), 200, catThieuNhi,
                "https://images.unsplash.com/photo-1629992101753-56d196c8aabb?w=400&h=600&fit=crop");
        createBook("Hoàng Tử Bé", "Antoine de Saint-Exupéry", "Câu chuyện triết lý",
                new BigDecimal("85000"), 120, catThieuNhi,
                "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=400&h=600&fit=crop");

        // Văn học Việt Nam
        Long catVanHocVN = categoryMap.get("Văn học Việt Nam");
        createBook("Đại Việt Sử Ký", "Ngô Sĩ Liên", "Bộ quốc sử Việt Nam",
                new BigDecimal("350000"), 20, catVanHocVN,
                "https://images.unsplash.com/photo-1461360370896-922624d12a74?w=400&h=600&fit=crop");

        // Lịch sử
        Long catLichSu = categoryMap.get("Lịch sử");
        createBook("Điện Biên Phủ", "Võ Nguyên Giáp", "Hồi ức chiến thắng lịch sử",
                new BigDecimal("185000"), 35, catLichSu,
                "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400&h=600&fit=crop");

        // Tâm lý - Triết học
        Long catTamLy = categoryMap.get("Tâm lý - Triết học");
        createBook("Đếch Quan Tâm", "Mark Manson", "Nghệ thuật sống ý nghĩa",
                new BigDecimal("139000"), 95, catTamLy,
                "https://images.unsplash.com/photo-1474631245212-32dc3c8310c6?w=400&h=600&fit=crop");
        createBook("Bạn Đắt Giá Bao Nhiêu", "Vãn Tình", "Sách về giá trị bản thân",
                new BigDecimal("89000"), 110, catTamLy,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=600&fit=crop");

        // Truyện tranh
        Long catTruyenTranh = categoryMap.get("Truyện tranh");
        createBook("One Piece Tập 1", "Eiichiro Oda", "Hành trình tìm kho báu",
                new BigDecimal("30000"), 150, catTruyenTranh,
                "https://images.unsplash.com/photo-1608889175123-8ee362201f81?w=400&h=600&fit=crop");
        createBook("Naruto Tập 1", "Masashi Kishimoto", "Câu chuyện ninja Naruto",
                new BigDecimal("30000"), 130, catTruyenTranh,
                "https://images.unsplash.com/photo-1613376023733-0a73315d9b06?w=400&h=600&fit=crop");
        createBook("Dragon Ball Tập 1", "Akira Toriyama", "Phiêu lưu của Goku",
                new BigDecimal("28000"), 100, catTruyenTranh,
                "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400&h=600&fit=crop");

        // 3. Tạo voucher mẫu
        createVoucher("WELCOME10", "Giảm 10% cho đơn hàng đầu tiên", new BigDecimal("10"), new BigDecimal("100000"), new BigDecimal("50000"), 1000, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(6));
        createVoucher("SAVE20", "Giảm 20% cho đơn hàng trên 300K", new BigDecimal("20"), new BigDecimal("200000"), new BigDecimal("300000"), 500, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(3));
        createVoucher("FLASH50", "Giảm 50% tối đa 500K", new BigDecimal("50"), new BigDecimal("500000"), new BigDecimal("1000000"), 50, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7));
        createVoucher("SUMMER15", "Giảm 15% mùa hè", new BigDecimal("15"), new BigDecimal("150000"), new BigDecimal("200000"), 300, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(2));
        createVoucher("BOOKLOVER", "Giảm 25% cho đơn hàng trên 500K", new BigDecimal("25"), new BigDecimal("300000"), new BigDecimal("500000"), 200, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMonths(4));

        logger.info("✅ Đã khởi tạo dữ liệu mẫu thành công!");
        logger.info("📊 Tổng số danh mục: {}", categoryRepository.count());
        logger.info("📚 Tổng số sách: {}", bookRepository.count());
        logger.info("🎟️ Tổng số voucher: {}", voucherRepository.count());
    }

    private void createCategory(String name, String description) {
        Long id = counterService.getNextSequence("categories");
        Category category = new Category(id, name, description);
        categoryRepository.save(category);
        logger.info("✅ Đã tạo danh mục: {} (ID: {})", name, id);
    }

    private void createBook(String title, String author, String description, BigDecimal price, int stock, Long categoryId, String imageUrl) {
        Long id = counterService.getNextSequence("books");
        Book book = new Book(id, title, author, description, price, stock, categoryId);
        book.setImageUrl(imageUrl);
        bookRepository.save(book);
        logger.info("✅ Đã tạo sách: {} - {} (ID: {})", title, author, id);
    }

    private void createVoucher(String code, String description, BigDecimal discountPercent, BigDecimal maxDiscount, BigDecimal minOrderAmount, int maxUsage, LocalDateTime validFrom, LocalDateTime validTo) {
        Voucher voucher = new Voucher(code, description, discountPercent, maxDiscount, minOrderAmount, maxUsage, validFrom, validTo);
        voucherRepository.save(voucher);
        logger.info("✅ Đã tạo voucher: {} - {} ({})", code, description, discountPercent + "%");
    }
}
