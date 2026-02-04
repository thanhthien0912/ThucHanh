package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Cart;
import com.phanthanhthien.cmp3025.bookstore.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Cart Controller - Xử lý giỏ hàng
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Controller
@RequestMapping("/giohang")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Xem giỏ hàng
     */
    @GetMapping
    public String viewCart(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String userId = authentication.getName();
        Cart cart = cartService.getCartByUserId(userId);

        model.addAttribute("cart", cart);
        model.addAttribute("pageTitle", "Giỏ hàng");
        model.addAttribute("currentPage", "cart");

        return "cart";
    }

    /**
     * Thêm sách vào giỏ hàng
     */
    @PostMapping("/them")
    public String addToCart(
            @RequestParam String bookId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            String userId = authentication.getName();
            cartService.addToCart(userId, bookId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sách vào giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/giohang";
    }

    /**
     * Cập nhật số lượng
     */
    @PostMapping("/capnhat")
    public String updateQuantity(
            @RequestParam String bookId,
            @RequestParam int quantity,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            String userId = authentication.getName();
            cartService.updateQuantity(userId, bookId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật số lượng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/giohang";
    }

    /**
     * Xóa sách khỏi giỏ hàng
     */
    @PostMapping("/xoa")
    public String removeFromCart(
            @RequestParam String bookId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            String userId = authentication.getName();
            cartService.removeFromCart(userId, bookId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sách khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/giohang";
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @PostMapping("/xoahet")
    public String clearCart(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            String userId = authentication.getName();
            cartService.clearCart(userId);
            redirectAttributes.addFlashAttribute("success", "Đã xóa toàn bộ giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/giohang";
    }
}
