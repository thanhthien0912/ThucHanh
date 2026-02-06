package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Cart;
import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import com.phanthanhthien.cmp3025.bookstore.services.CartService;
import com.phanthanhthien.cmp3025.bookstore.services.MomoPaymentService;
import com.phanthanhthien.cmp3025.bookstore.services.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private VoucherService voucherService;

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
    public String payWithMomo(
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String receiverAddress,
            @RequestParam(required = false) String voucherCode,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        logger.info("=== Payment Request ===");
        logger.info("Receiver Name: {}", receiverName);
        logger.info("Receiver Phone: {}", receiverPhone);
        logger.info("Receiver Address: {}", receiverAddress);
        logger.info("Voucher Code: {}", voucherCode);

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

            // Validate thông tin người nhận
            if (receiverName == null || receiverName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập tên người nhận!");
                return "redirect:/thanhtoan";
            }
            if (receiverPhone == null || receiverPhone.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập số điện thoại!");
                return "redirect:/thanhtoan";
            }
            if (receiverAddress == null || receiverAddress.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập địa chỉ giao hàng!");
                return "redirect:/thanhtoan";
            }

            // Tính tổng tiền và áp dụng voucher nếu có
            BigDecimal totalAmount = cart.getTotalAmount();
            BigDecimal discountAmount = BigDecimal.ZERO;
            Voucher voucher = null;

            logger.info("Cart Total Amount: {}", totalAmount);

            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                logger.info("🔍 Validating voucher: {}", voucherCode);
                // Validate voucher
                Optional<Voucher> voucherOpt = voucherService.validateVoucher(voucherCode, totalAmount);
                if (voucherOpt.isPresent()) {
                    voucher = voucherOpt.get();
                    discountAmount = voucherService.calculateDiscount(voucher, totalAmount);
                    logger.info("✅ Voucher valid! Code: {}, Discount: {}", voucherCode, discountAmount);
                } else {
                    logger.warn("❌ Voucher invalid: {}", voucherCode);
                    redirectAttributes.addFlashAttribute("error", "Mã voucher không hợp lệ!");
                    return "redirect:/thanhtoan";
                }
            } else {
                logger.info("ℹ️ No voucher code provided");
            }

            BigDecimal finalAmount = totalAmount.subtract(discountAmount);
            logger.info("💰 Final Amount: {} (Total: {} - Discount: {})", finalAmount, totalAmount, discountAmount);

            // Tạo đơn hàng
            Order order = new Order(userId, userId, cart.getItems(), totalAmount);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setReceiverAddress(receiverAddress);

            if (voucher != null) {
                order.setVoucherId(voucher.getId());
                order.setVoucherCode(voucher.getCode());
                order.setDiscountAmount(discountAmount);
                order.setFinalAmount(finalAmount);

                // Tăng số lần sử dụng voucher
                voucherService.incrementUsage(voucher.getId());
            } else {
                order.setFinalAmount(totalAmount);
            }

            order.setPaymentMethod("MOMO");
            order = orderRepository.save(order);

            logger.info("📦 Tạo đơn hàng: {} - Người nhận: {} - Tổng tiền: {} - Giảm: {} - Thành tiền: {}",
                    order.getId(), receiverName, totalAmount, discountAmount, finalAmount);

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
            logger.error("❌ Lỗi thanh toán MoMo: ", e);
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
        Order order = (orderId != null && !orderId.isEmpty())
                ? orderRepository.findById(orderId).orElse(null)
                : null;

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
        Order order = (orderId != null && !orderId.isEmpty())
                ? orderRepository.findById(orderId).orElse(null)
                : null;

        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Thanh toán thất bại");
        model.addAttribute("currentPage", "order-failed");

        return "order-failed";
    }

    /**
     * API validate voucher (AJAX)
     */
    @PostMapping("/validate-voucher")
    @ResponseBody
    public Map<String, Object> validateVoucher(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập");
            return response;
        }

        try {
            String code = request.getOrDefault("code", "");
            String orderAmountStr = request.getOrDefault("orderAmount", "");

            if (code == null || code.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng nhập mã voucher");
                return response;
            }

            BigDecimal orderAmount = (orderAmountStr != null && !orderAmountStr.isEmpty())
                    ? new BigDecimal(orderAmountStr)
                    : BigDecimal.ZERO;

            // Validate voucher
            Optional<Voucher> voucherOpt = voucherService.validateVoucher(code, orderAmount);

            if (voucherOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mã voucher không hợp lệ hoặc đã hết hạn");
                return response;
            }

            Voucher voucher = voucherOpt.get();
            BigDecimal discountAmount = voucherService.calculateDiscount(voucher, orderAmount);
            BigDecimal finalAmount = orderAmount.subtract(discountAmount);

            response.put("success", true);
            response.put("message", "Áp dụng voucher thành công");
            response.put("voucher", voucher);
            response.put("discountAmount", discountAmount);
            response.put("finalAmount", finalAmount);

            logger.info("✅ Voucher valid: {} - Discount: {}, Final: {}", code, discountAmount, finalAmount);

        } catch (Exception e) {
            logger.error("❌ Error validating voucher: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }
}
