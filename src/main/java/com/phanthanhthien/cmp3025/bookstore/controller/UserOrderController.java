package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * User Order Controller - Xử lý xem đơn hàng của người dùng
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Controller
@RequestMapping("/user/orders")
public class UserOrderController {

    private static final Logger logger = LoggerFactory.getLogger(UserOrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Trang danh sách đơn hàng của user
     */
    @GetMapping
    public String listOrders(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/dangnhap";
        }

        String userId = authentication.getName();
        logger.info("📦 User {} đang xem danh sách đơn hàng", userId);

        // Lấy tất cả đơn hàng của user, sắp xếp theo thời gian mới nhất
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Đơn hàng của tôi");
        model.addAttribute("currentPage", "user-orders");

        return "user/orders";
    }

    /**
     * Trang chi tiết đơn hàng
     */
    @GetMapping("/{orderId}")
    public String orderDetail(
            @PathVariable String orderId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/dangnhap";
        }

        String userId = authentication.getName();
        logger.info("📦 User {} đang xem chi tiết đơn hàng {}", userId, orderId);

        // Lấy đơn hàng
        Order order = (orderId != null && !orderId.isEmpty())
                ? orderRepository.findById(orderId).orElse(null)
                : null;

        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/user/orders";
        }

        // Kiểm tra xem đơn hàng có thuộc về user này không
        if (!order.getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/user/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + orderId);
        model.addAttribute("currentPage", "user-order-detail");

        return "user/order-detail";
    }
}
