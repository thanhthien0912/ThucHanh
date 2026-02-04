package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MoMo Payment Service - Tích hợp thanh toán MoMo UAT
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Service
public class MomoPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MomoPaymentService.class);

    @Value("${momo.partner-code:MOMOBKUN20180529}")
    private String partnerCode;

    @Value("${momo.access-key:klm05TvNBzhg7h7j}")
    private String accessKey;

    @Value("${momo.secret-key:at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa}")
    private String secretKey;

    @Value("${momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String endpoint;

    @Value("${momo.redirect-url:http://localhost:8080/thanhtoan/momo/callback}")
    private String redirectUrl;

    @Value("${momo.ipn-url:http://localhost:8080/thanhtoan/momo/notify}")
    private String ipnUrl;

    @Autowired
    private OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Tạo yêu cầu thanh toán MoMo
     */
    public Map<String, Object> createPayment(Order order) {
        try {
            // Tạo orderId duy nhất bằng cách thêm timestamp để tránh trùng lặp
            String orderId = order.getId() + "_" + System.currentTimeMillis();
            String requestId = UUID.randomUUID().toString();
            String amount = String.valueOf(order.getTotalAmount().longValue());
            String orderInfo = "Thanh toán đơn hàng #" + orderId;
            String requestType = "payWithMethod";
            String extraData = "";

            // Tạo raw signature
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId,
                    requestType);

            // Tạo chữ ký HMAC SHA256
            String signature = hmacSHA256(rawSignature, secretKey);

            // Tạo request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("partnerName", "Bookstore");
            requestBody.put("storeId", "BookstoreStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", Long.parseLong(amount));
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", redirectUrl);
            requestBody.put("ipnUrl", ipnUrl);
            requestBody.put("lang", "vi");
            requestBody.put("requestType", requestType);
            requestBody.put("autoCapture", true);
            requestBody.put("extraData", extraData);
            requestBody.put("signature", signature);

            // Cập nhật order với requestId
            order.setMomoRequestId(requestId);
            order.setOrderInfo(orderInfo);
            orderRepository.save(order);

            logger.info("📤 Gửi yêu cầu thanh toán MoMo cho đơn hàng: {}", orderId);

            // Gọi API MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    Map.class);

            Map<String, Object> responseBody = response.getBody();
            logger.info("📥 Phản hồi từ MoMo: {}", responseBody);

            return responseBody;

        } catch (Exception e) {
            logger.error("❌ Lỗi tạo thanh toán MoMo: {}", e.getMessage());
            throw new RuntimeException("Lỗi tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xử lý callback từ MoMo
     */
    public boolean processCallback(String orderId, String requestId, int resultCode, String transId) {
        try {
            // Trích xuất orderId gốc (bỏ phần timestamp nếu có)
            String originalOrderId = orderId;
            if (orderId.contains("_")) {
                originalOrderId = orderId.substring(0, orderId.lastIndexOf("_"));
            }

            Order order = orderRepository.findById(originalOrderId).orElse(null);
            if (order == null) {
                logger.error("❌ Không tìm thấy đơn hàng: {}", originalOrderId);
                return false;
            }

            if (resultCode == 0) {
                // Thanh toán thành công
                order.setPaymentStatus("SUCCESS");
                order.setMomoTransId(transId);
                order.setPaidAt(LocalDateTime.now());
                logger.info("✅ Thanh toán thành công cho đơn hàng: {}", orderId);
            } else {
                // Thanh toán thất bại
                order.setPaymentStatus("FAILED");
                logger.warn("❌ Thanh toán thất bại cho đơn hàng: {}, mã lỗi: {}", orderId, resultCode);
            }

            orderRepository.save(order);
            return resultCode == 0;

        } catch (Exception e) {
            logger.error("❌ Lỗi xử lý callback: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Xác thực chữ ký từ MoMo
     */
    public boolean verifySignature(Map<String, String> params, String signature) {
        try {
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                    accessKey,
                    params.get("amount"),
                    params.get("extraData"),
                    params.get("message"),
                    params.get("orderId"),
                    params.get("orderInfo"),
                    params.get("orderType"),
                    partnerCode,
                    params.get("payType"),
                    params.get("requestId"),
                    params.get("responseTime"),
                    params.get("resultCode"),
                    params.get("transId"));

            String computedSignature = hmacSHA256(rawSignature, secretKey);
            return computedSignature.equals(signature);

        } catch (Exception e) {
            logger.error("❌ Lỗi xác thực chữ ký: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Tạo HMAC SHA256
     */
    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
