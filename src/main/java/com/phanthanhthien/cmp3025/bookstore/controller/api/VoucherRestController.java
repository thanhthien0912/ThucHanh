package com.phanthanhthien.cmp3025.bookstore.controller.api;

import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import com.phanthanhthien.cmp3025.bookstore.services.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * VoucherRestController - REST API cho Quản lý Voucher
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vouchers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VoucherRestController {

    @Autowired
    private VoucherService voucherService;

    /**
     * GET /api/v1/vouchers - Lấy danh sách tất cả vouchers
     */
    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.findAll());
    }

    /**
     * GET /api/v1/vouchers/active - Lấy danh sách vouchers đang hoạt động
     */
    @GetMapping("/active")
    public ResponseEntity<List<Voucher>> getActiveVouchers() {
        return ResponseEntity.ok(voucherService.findActiveVouchers());
    }

    /**
     * GET /api/v1/vouchers/{id} - Lấy thông tin voucher theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable String id) {
        Optional<Voucher> voucher = voucherService.findById(id);
        if (voucher.isPresent()) {
            return ResponseEntity.ok(voucher.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Không tìm thấy voucher với ID: " + id);
        return ResponseEntity.status(404).body(error);
    }

    /**
     * GET /api/v1/vouchers/code/{code} - Tìm voucher theo mã code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getVoucherByCode(@PathVariable String code) {
        Optional<Voucher> voucher = voucherService.findByCode(code);
        if (voucher.isPresent()) {
            return ResponseEntity.ok(voucher.get());
        }
        Map<String, String> error = new HashMap<>();
        error.put("error", "Không tìm thấy voucher với mã: " + code);
        return ResponseEntity.status(404).body(error);
    }

    /**
     * POST /api/v1/vouchers - Tạo voucher mới
     */
    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody Voucher voucher) {
        try {
            // Validate
            if (voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mã voucher không được để trống");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucher.getDiscountPercent() == null || voucher.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phần trăm giảm giá phải lớn hơn 0");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucher.getDiscountPercent().compareTo(BigDecimal.valueOf(100)) >= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phần trăm giảm giá phải nhỏ hơn 100");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucher.getValidFrom() == null || voucher.getValidTo() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu và ngày kết thúc không được để trống");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucher.getValidFrom().isAfter(voucher.getValidTo())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucher.getMaxUsage() == null || voucher.getMaxUsage() <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Số lần sử dụng tối đa phải lớn hơn 0");
                return ResponseEntity.badRequest().body(error);
            }

            Voucher savedVoucher = voucherService.create(voucher);
            return ResponseEntity.ok(savedVoucher);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi tạo voucher: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * PUT /api/v1/vouchers/{id} - Cập nhật voucher
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(
            @PathVariable String id,
            @RequestBody Voucher voucherData) {

        try {
            Optional<Voucher> existingVoucher = voucherService.findById(id);
            if (existingVoucher.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không tìm thấy voucher với ID: " + id);
                return ResponseEntity.status(404).body(error);
            }

            // Validate
            if (voucherData.getCode() == null || voucherData.getCode().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mã voucher không được để trống");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucherData.getDiscountPercent() == null || voucherData.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phần trăm giảm giá phải lớn hơn 0");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucherData.getDiscountPercent().compareTo(BigDecimal.valueOf(100)) >= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phần trăm giảm giá phải nhỏ hơn 100");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucherData.getValidFrom() == null || voucherData.getValidTo() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu và ngày kết thúc không được để trống");
                return ResponseEntity.badRequest().body(error);
            }

            if (voucherData.getValidFrom().isAfter(voucherData.getValidTo())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu phải trước ngày kết thúc");
                return ResponseEntity.badRequest().body(error);
            }

            Voucher updatedVoucher = voucherService.update(id, voucherData);
            return ResponseEntity.ok(updatedVoucher);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi cập nhật voucher: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * DELETE /api/v1/vouchers/{id} - Xóa voucher
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVoucher(@PathVariable String id) {
        Optional<Voucher> voucher = voucherService.findById(id);

        Map<String, String> response = new HashMap<>();
        if (voucher.isPresent()) {
            voucherService.deleteById(id);
            response.put("message", "Xóa voucher thành công");
            response.put("id", id);
            return ResponseEntity.ok(response);
        }

        response.put("message", "Không tìm thấy voucher");
        return ResponseEntity.status(404).body(response);
    }

    /**
     * POST /api/v1/vouchers/validate - Xác thực voucher và tính giảm giá
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateVoucher(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            BigDecimal orderAmount = request.get("orderAmount") != null 
                ? new BigDecimal(request.get("orderAmount").toString()) 
                : null;

            if (code == null || code.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Mã voucher không được để trống");
                return ResponseEntity.badRequest().body(error);
            }

            Optional<Voucher> voucherOpt = voucherService.validateVoucher(code, orderAmount);

            if (voucherOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Voucher không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.badRequest().body(error);
            }

            Voucher voucher = voucherOpt.get();
            BigDecimal discountAmount = voucherService.calculateDiscount(voucher, orderAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("voucher", voucher);
            response.put("discountAmount", discountAmount);
            response.put("message", "Voucher hợp lệ");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi xác thực voucher: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
