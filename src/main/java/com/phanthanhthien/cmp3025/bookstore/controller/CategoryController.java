package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CounterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoryController - Quản lý Danh mục
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/danhmuc")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CounterService counterService;

    /**
     * Xem danh sách tất cả danh mục
     */
    @GetMapping({ "", "/" })
    public String listCategories(Model model) {
        log.info("Loading all categories...");
        List<Category> categories = categoryRepository.findAll();
        log.info("Found {} categories", categories.size());
        categories.forEach(cat -> log.info("Category: ID={}, Name={}", cat.getId(), cat.getName()));

        model.addAttribute("pageTitle", "Quản lý Danh mục");
        model.addAttribute("currentPage", "danhmuc");
        model.addAttribute("categories", categories);
        model.addAttribute("totalCategories", categoryRepository.count());

        log.info("Model attributes - categories: {}, totalCategories: {}",
                model.containsAttribute("categories") ? categories.size() : "NULL",
                model.getAttribute("totalCategories"));

        return "danhmuc/index";
    }

    /**
     * Hiển thị form thêm danh mục mới
     */
    @GetMapping("/them")
    public String addCategoryForm(Model model) {
        model.addAttribute("pageTitle", "Thêm danh mục mới");
        model.addAttribute("currentPage", "danhmuc");
        model.addAttribute("category", new Category());
        return "danhmuc/form";
    }

    /**
     * Xử lý thêm danh mục mới
     */
    @PostMapping("/them")
    public String addCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm danh mục mới");
            model.addAttribute("currentPage", "danhmuc");
            return "danhmuc/form";
        }

        // Kiểm tra tên danh mục đã tồn tại chưa
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            result.rejectValue("name", "error.category", "Tên danh mục này đã tồn tại");
            model.addAttribute("pageTitle", "Thêm danh mục mới");
            model.addAttribute("currentPage", "danhmuc");
            return "danhmuc/form";
        }

        // Lấy ID tự động tăng cho danh mục mới
        Long newId = counterService.getNextSequence("categories");
        category.setId(newId);

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage",
                "Thêm danh mục \"" + category.getName() + "\" thành công!");

        return "redirect:/danhmuc";
    }

    /**
     * Hiển thị form sửa danh mục
     */
    @GetMapping("/sua/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy danh mục với ID: " + id);
            return "redirect:/danhmuc";
        }

        model.addAttribute("pageTitle", "Sửa danh mục");
        model.addAttribute("currentPage", "danhmuc");
        model.addAttribute("category", categoryOpt.get());
        model.addAttribute("isEdit", true);

        return "danhmuc/form";
    }

    /**
     * Xử lý cập nhật danh mục
     */
    @PostMapping("/sua/{id}")
    public String updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Sửa danh mục");
            model.addAttribute("currentPage", "danhmuc");
            model.addAttribute("isEdit", true);
            return "danhmuc/form";
        }

        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy danh mục với ID: " + id);
            return "redirect:/danhmuc";
        }

        category.setId(id);
        category.onUpdate();
        categoryRepository.save(category);

        redirectAttributes.addFlashAttribute("successMessage",
                "Cập nhật danh mục \"" + category.getName() + "\" thành công!");

        return "redirect:/danhmuc";
    }

    /**
     * Xóa danh mục và các sách liên quan
     */
    @GetMapping("/xoa/{id}")
    public String deleteCategory(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();

            // Tìm và xoá các sách thuộc danh mục này
            List<Book> relatedBooks = bookRepository.findByCategoryId(id);
            int bookCount = relatedBooks.size();

            if (!relatedBooks.isEmpty()) {
                // Lấy tên các sách bị xoá để hiển thị
                String deletedBookTitles = relatedBooks.stream()
                        .map(Book::getTitle)
                        .collect(Collectors.joining(", "));

                // Xoá tất cả sách liên quan
                bookRepository.deleteAll(relatedBooks);

                // Xoá danh mục
                categoryRepository.deleteById(id);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã xóa danh mục \"" + category.getName() + "\" cùng với " + bookCount + " sách: "
                                + deletedBookTitles);
            } else {
                // Không có sách liên quan, chỉ xoá danh mục
                categoryRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã xóa danh mục \"" + category.getName() + "\"");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy danh mục với ID: " + id);
        }

        return "redirect:/danhmuc";
    }

}
