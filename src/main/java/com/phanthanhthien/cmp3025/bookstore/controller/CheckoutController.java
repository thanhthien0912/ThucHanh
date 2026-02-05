package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Cart;
import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CartService;
import com.phanthanhthien.cmp3025.bookstore.services.MomoPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Checkout Controller - Xử lý thanh toán
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Controller
@RequestMapping("/thanhtoan")
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MomoPaymentService momoPaymentService;

    /**
     * Trang thanh toán
     */
    @GetMapping
    public String checkoutPage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/dangnhap";
        }

        String userId = authentication.getName();
        Cart cart = cartService.getCartByUserId(userId);

        if (cart.getItems().isEmpty()) {
            return "redirect:/giohang";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("pageTitle", "Thanh toán");
        model.addAttribute("currentPage", "checkout");

        return "checkout";
    }

    /**
     * Tạo đơn hàng và thanh toán MoMo
     */
    @PostMapping("/momo")
    public String payWithMomo(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/dangnhap";
        }

        try {
            String userId = authentication.getName();
            Cart cart = cartService.getCartByUserId(userId);

            if (cart.getItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
                return "redirect:/giohang";
            }

            // Tạo đơn hàng
            Order order = new Order(userId, userId, cart.getItems(), cart.getTotalAmount());
            order.setPaymentMethod("MOMO");
            order = orderRepository.save(order);

            logger.info("📦 Tạo đơn hàng: {} với tổng tiền: {}", order.getId(), order.getTotalAmount());

            // Gọi API MoMo
            Map<String, Object> momoResponse = momoPaymentService.createPayment(order);

            if (momoResponse != null && momoResponse.get("resultCode") != null) {
                int resultCode = (int) momoResponse.get("resultCode");

                if (resultCode == 0) {
                    String payUrl = (String) momoResponse.get("payUrl");
                    logger.info("🔗 Redirect đến MoMo: {}", payUrl);
                    return "redirect:" + payUrl;
                } else {
                    String message = (String) momoResponse.get("message");
                    logger.error("❌ MoMo trả về lỗi: {}", message);
                    redirectAttributes.addFlashAttribute("error", "Lỗi MoMo: " + message);
                    return "redirect:/thanhtoan";
                }
            }

            redirectAttributes.addFlashAttribute("error", "Không nhận được phản hồi từ MoMo!");
            return "redirect:/thanhtoan";

        } catch (Exception e) {
            logger.error("❌ Lỗi thanh toán MoMo: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/thanhtoan";
        }
    }

    /**
     * Callback từ MoMo (redirect URL)
     */
    @GetMapping("/momo/callback")
    public String momoCallback(
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam int resultCode,
            @RequestParam(required = false) String transId,
            @RequestParam(required = false) String message,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        logger.info("📥 MoMo Callback - orderId: {}, resultCode: {}, transId: {}", orderId, resultCode, transId);

        boolean success = momoPaymentService.processCallback(orderId, requestId, resultCode, transId);

        if (success) {
            // Xóa giỏ hàng sau khi thanh toán thành công
            if (authentication != null) {
                cartService.clearCart(authentication.getName());
            }
            return "redirect:/thanhtoan/thanhcong?orderId=" + orderId;
        } else {
            redirectAttributes.addFlashAttribute("error", "Thanh toán thất bại: " + message);
            return "redirect:/thanhtoan/thatbai?orderId=" + orderId;
        }
    }

    /**
     * IPN (Instant Payment Notification) từ MoMo
     */
    @PostMapping("/momo/notify")
    @ResponseBody
    public Map<String, Object> momoIPN(@RequestBody Map<String, Object> payload) {
        logger.info("📥 MoMo IPN: {}", payload);

        try {
            String orderId = (String) payload.get("orderId");
            String requestId = (String) payload.get("requestId");
            int resultCode = (int) payload.get("resultCode");
            String transId = payload.get("transId") != null ? payload.get("transId").toString() : null;

            momoPaymentService.processCallback(orderId, requestId, resultCode, transId);

            return Map.of("resultCode", 0, "message", "OK");
        } catch (Exception e) {
            logger.error("❌ Lỗi xử lý IPN: {}", e.getMessage());
            return Map.of("resultCode", 1, "message", e.getMessage());
        }
    }

    /**
     * Trang thanh toán thành công
     */
    @GetMapping("/thanhcong")
    public String orderSuccess(@RequestParam String orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Đặt hàng thành công");
        model.addAttribute("currentPage", "order-success");

        return "order-success";
    }

    /**
     * Trang thanh toán thất bại
     */
    @GetMapping("/thatbai")
    public String orderFailed(@RequestParam String orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Thanh toán thất bại");
        model.addAttribute("currentPage", "order-failed");

        return "order-failed";
    }
}
