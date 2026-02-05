package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CounterService;
import com.phanthanhthien.cmp3025.bookstore.services.ExcelExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * BookController - Quản lý Sách
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Controller
@RequestMapping("/sach")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CounterService counterService;

    /**
     * Xem danh sách tất cả sách
     */
    @GetMapping({ "", "/" })
    public String listBooks(Model model) {
        model.addAttribute("pageTitle", "Quản lý Sách");
        model.addAttribute("currentPage", "sach");
        model.addAttribute("books", bookRepository.findAll());
        model.addAttribute("totalBooks", bookRepository.count());
        return "sach/index";
    }

    /**
     * Tìm kiếm sách
     */
    @GetMapping("/timkiem")
    public String searchBooks(Model model, @RequestParam String q) {
        model.addAttribute("pageTitle", "Tìm kiếm: " + q);
        model.addAttribute("currentPage", "sach");
        model.addAttribute("keyword", q);

        List<Book> searchResults = bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q);
        model.addAttribute("books", searchResults);
        model.addAttribute("totalBooks", searchResults.size());

        return "sach/index";
    }

    /**
     * Hiển thị form thêm sách mới
     */
    @GetMapping("/them")
    public String addBookForm(Model model) {
        model.addAttribute("pageTitle", "Thêm sách mới");
        model.addAttribute("currentPage", "sach");
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "sach/form";
    }

    /**
     * Xử lý thêm sách mới
     */
    @PostMapping("/them")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm sách mới");
            model.addAttribute("currentPage", "sach");
            model.addAttribute("categories", categoryRepository.findAll());
            return "sach/form";
        }

        // Lấy ID tự động tăng cho sách mới
        Long newId = counterService.getNextSequence("books");
        book.setId(newId);

        bookRepository.save(book);
        redirectAttributes.addFlashAttribute("successMessage",
                "Thêm sách \"" + book.getTitle() + "\" thành công!");

        return "redirect:/sach";
    }

    /**
     * Hiển thị form sửa sách
     */
    @GetMapping("/sua/{id}")
    public String editBookForm(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy sách với ID: " + id);
            return "redirect:/sach";
        }

        model.addAttribute("pageTitle", "Sửa thông tin sách");
        model.addAttribute("currentPage", "sach");
        model.addAttribute("book", bookOpt.get());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isEdit", true);

        return "sach/form";
    }

    /**
     * Xử lý cập nhật sách
     */
    @PostMapping("/sua/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Sửa thông tin sách");
            model.addAttribute("currentPage", "sach");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("isEdit", true);
            return "sach/form";
        }

        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy sách với ID: " + id);
            return "redirect:/sach";
        }

        book.setId(id);
        book.onUpdate();
        bookRepository.save(book);

        redirectAttributes.addFlashAttribute("successMessage",
                "Cập nhật sách \"" + book.getTitle() + "\" thành công!");

        return "redirect:/sach";
    }

    /**
     * Xóa sách
     */
    @GetMapping("/xoa/{id}")
    public String deleteBook(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isPresent()) {
            bookRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xóa sách \"" + bookOpt.get().getTitle() + "\"");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy sách với ID: " + id);
        }

        return "redirect:/sach";
    }

    /**
     * Xem chi tiết sách
     */
    @GetMapping("/chi-tiet/{id}")
    public String viewBook(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy sách với ID: " + id);
            return "redirect:/sach";
        }

        model.addAttribute("pageTitle", bookOpt.get().getTitle());
        model.addAttribute("currentPage", "sach");
        model.addAttribute("book", bookOpt.get());

        // Lấy thông tin danh mục nếu có
        if (bookOpt.get().getCategoryId() != null) {
            categoryRepository.findById(bookOpt.get().getCategoryId())
                    .ifPresent(cat -> model.addAttribute("category", cat));
        }

        return "sach/detail";
    }

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * Xuất danh sách sách ra file Excel
     */
    @GetMapping("/xuat-excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            byte[] excelData = excelExportService.exportBooksToExcel();

            String filename = "DanhSachSach_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
