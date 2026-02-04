package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AdminController - Quản trị hệ thống
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Controller
public class AdminController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Trang quản lý ngườ dùng
     */
    @GetMapping("/nguoidung")
    public String userManagement(Model model) {
        model.addAttribute("pageTitle", "Quản lý Ngườ dùng");
        model.addAttribute("currentPage", "nguoidung");
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    /**
     * Bảng điều khiển quản trị
     */
    @GetMapping("/quantri")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Bảng điều khiển Admin");
        model.addAttribute("currentPage", "quantri");

        // Lấy số liệu thống kê từ MongoDB
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalOrders", 0); // Sẽ cập nhật sau
        model.addAttribute("totalUsers", userRepository.count());

        return "admin/dashboard";
    }

    /**
     * API Documentation Page
     */
    @GetMapping("/apidocs")
    public String apiDocs(Model model) {
        model.addAttribute("pageTitle", "API Documentation");
        model.addAttribute("currentPage", "api");
        return "admin/api";
    }

}
