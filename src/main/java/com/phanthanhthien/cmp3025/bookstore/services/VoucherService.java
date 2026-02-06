package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import com.phanthanhthien.cmp3025.bookstore.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * VoucherService - Business Logic cho Voucher
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    /**
     * Lấy tất cả vouchers
     */
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    /**
     * Lấy tất cả vouchers đang hoạt động
     */
    public List<Voucher> findActiveVouchers() {
        return voucherRepository.findByIsActiveTrue();
    }

    /**
     * Tìm voucher theo ID
     */
    public Optional<Voucher> findById(String id) {
        return voucherRepository.findById(id);
    }

    /**
     * Tìm voucher theo code
     */
    public Optional<Voucher> findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    /**
     * Tạo voucher mới
     */
    public Voucher create(Voucher voucher) {
        if (voucherRepository.existsByCode(voucher.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại!");
        }
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setUpdatedAt(LocalDateTime.now());
        return voucherRepository.save(voucher);
    }

    /**
     * Cập nhật voucher
     */
    public Voucher update(String id, Voucher voucherDetails) {
        Optional<Voucher> existingVoucher = voucherRepository.findById(id);
        if (existingVoucher.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy voucher với ID: " + id);
        }

        Voucher voucher = existingVoucher.get();
        voucher.setCode(voucherDetails.getCode());
        voucher.setDescription(voucherDetails.getDescription());
        voucher.setDiscountPercent(voucherDetails.getDiscountPercent());
        voucher.setMaxDiscountAmount(voucherDetails.getMaxDiscountAmount());
        voucher.setMinOrderAmount(voucherDetails.getMinOrderAmount());
        voucher.setMaxUsage(voucherDetails.getMaxUsage());
        voucher.setValidFrom(voucherDetails.getValidFrom());
        voucher.setValidTo(voucherDetails.getValidTo());
        voucher.setIsActive(voucherDetails.getIsActive());
        voucher.onUpdate();

        return voucherRepository.save(voucher);
    }

    /**
     * Xóa voucher
     */
    public void deleteById(String id) {
        voucherRepository.deleteById(id);
    }

    /**
     * Xác thực voucher
     */
    public Optional<Voucher> validateVoucher(String code, BigDecimal orderAmount) {
        Optional<Voucher> voucherOpt = voucherRepository.findByCode(code);

        if (voucherOpt.isEmpty()) {
            return Optional.empty();
        }

        Voucher voucher = voucherOpt.get();

        // Kiểm tra voucher có hợp lệ không
        if (!voucher.getIsActive()) {
            return Optional.empty();
        }

        // Kiểm tra đã hết lượt sử dụng chưa
        if (voucher.getCurrentUsage() >= voucher.getMaxUsage()) {
            return Optional.empty();
        }

        // Kiểm tra ngày hiệu lực
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getValidFrom()) || now.isAfter(voucher.getValidTo())) {
            return Optional.empty();
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (orderAmount != null && voucher.getMinOrderAmount() != null &&
            orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            return Optional.empty();
        }

        return Optional.of(voucher);
    }

    /**
     * Tính số tiền giảm giá
     */
    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderAmount) {
        if (voucher == null || orderAmount == null) {
            return BigDecimal.ZERO;
        }

        // Tính số tiền giảm theo phần trăm
        BigDecimal discount = orderAmount.multiply(voucher.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));

        // Áp dụng giới hạn giảm giá tối đa
        if (voucher.getMaxDiscountAmount() != null) {
            discount = discount.min(voucher.getMaxDiscountAmount());
        }

        return discount;
    }

    /**
     * Tăng số lần sử dụng voucher
     */
    public void incrementUsage(String voucherId) {
        Optional<Voucher> voucherOpt = voucherRepository.findById(voucherId);
        if (voucherOpt.isPresent()) {
            Voucher voucher = voucherOpt.get();
            voucher.setCurrentUsage(voucher.getCurrentUsage() + 1);
            voucher.onUpdate();
            voucherRepository.save(voucher);
        }
    }

    /**
     * Đếm tổng số voucher
     */
    public long count() {
        return voucherRepository.count();
    }
}
