package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import com.phanthanhthien.cmp3025.bookstore.services.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Admin Order Controller - Quản lý đơn hàng
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Controller
@RequestMapping("/quantri/donhang")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * Danh sách tất cả đơn hàng
     */
    @GetMapping({ "", "/" })
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAll();

        // Sắp xếp theo ngày tạo mới nhất
        orders.sort((a, b) -> {
            if (a.getCreatedAt() == null)
                return 1;
            if (b.getCreatedAt() == null)
                return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Quản lý Đơn hàng");
        model.addAttribute("currentPage", "donhang");

        // Thống kê
        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> "PENDING".equals(o.getPaymentStatus())).count();
        long successOrders = orders.stream().filter(o -> "SUCCESS".equals(o.getPaymentStatus())).count();
        long completedOrders = orders.stream().filter(o -> "COMPLETED".equals(o.getOrderStatus())).count();

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("successOrders", successOrders);
        model.addAttribute("completedOrders", completedOrders);

        return "quantri/donhang/index";
    }

    /**
     * Xem chi tiết đơn hàng
     */
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isEmpty()) {
            return "redirect:/quantri/donhang";
        }

        model.addAttribute("order", orderOpt.get());
        model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + id);
        model.addAttribute("currentPage", "donhang");

        return "quantri/donhang/detail";
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable String id,
            @RequestParam String orderStatus,
            RedirectAttributes redirectAttributes) {

        Optional<Order> orderOpt = orderRepository.findById(id);

        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/quantri/donhang";
        }

        Order order = orderOpt.get();
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã cập nhật trạng thái đơn hàng #" + id + " thành " + getStatusLabel(orderStatus));

        return "redirect:/quantri/donhang/" + id;
    }

    private String getStatusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "Chờ xử lý";
            case "PROCESSING" -> "Đang xử lý";
            case "SHIPPED" -> "Đang giao";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    /**
     * Xuất danh sách đơn hàng ra Excel
     */
    @GetMapping("/xuat-excel")
    public ResponseEntity<byte[]> exportOrdersToExcel() {
        try {
            byte[] excelBytes = excelExportService.exportOrdersToExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(
                    "attachment",
                    java.net.URLEncoder.encode("danh-sach-don-hang.xlsx", java.nio.charset.StandardCharsets.UTF_8)
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
