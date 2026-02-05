package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController - Điều hướng trang chủ
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 * @since 2024
 */
@Controller
public class HomeController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Xử lý request GET cho đường dẫn "/" và "/home"
     *
     * @param model Model để truyền dữ liệu sang view
     * @param authentication Authentication object để kiểm tra role
     * @return Tên view "home" để Thymeleaf render
     */
    @GetMapping({ "/", "/home" })
    public String home(Model model, Authentication authentication) {
        // Truyền thông tin cơ bản sang view
        model.addAttribute("pageTitle", "Trang chủ - Hệ thống Quản lý Nhà sách");
        model.addAttribute("currentPage", "home");

        // Lấy số liệu thống kê từ MongoDB
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalOrders", 0); // Sẽ cập nhật khi có Order repository
        model.addAttribute("totalUsers", 0); // Sẽ cập nhật khi có User repository

        // Lấy danh sách danh mục
        model.addAttribute("categories", categoryRepository.findAll());

        // Chỉ truyền books cho người dùng thường (không phải ADMIN)
        // Admin không cần xem sách trên trang chủ, họ có trang quản lý riêng
        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("books", bookRepository.findAll()
                    .stream()
                    .limit(12)
                    .toList());
        }

        return "home";
    }

    /**
     * Xử lý trang đăng nhập
     * 
     * @return Tên view "login"
     */
    @GetMapping("/dangnhap")
    public String login() {
        return "login";
    }

    /**
     * Xử lý trang Access Denied (Từ chối truy cập)
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Tên view "error/403"
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này!");
        return "error/403";
    }

    /**
     * Xử lý trang đăng ký
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Tên view "register"
     */
    @GetMapping("/dangky")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Đăng ký tài khoản");
        return "register";
    }

}
