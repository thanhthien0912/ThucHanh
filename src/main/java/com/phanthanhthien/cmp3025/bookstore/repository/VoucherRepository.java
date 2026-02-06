package com.phanthanhthien.cmp3025.bookstore.repository;

import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Voucher Repository - Truy vấn MongoDB cho collection vouchers
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Repository
public interface VoucherRepository extends MongoRepository<Voucher, String> {

    /**
     * Tìm voucher theo mã code
     */
    Optional<Voucher> findByCode(String code);

    /**
     * Tìm tất cả voucher đang hoạt động
     */
    List<Voucher> findByIsActiveTrue();

    /**
     * Tìm voucher đang hoạt động và còn trong hạn sử dụng
     */
    List<Voucher> findByIsActiveTrueAndValidToBefore(java.time.LocalDateTime dateTime);

    /**
     * Kiểm tra code đã tồn tại chưa
     */
    boolean existsByCode(String code);
}
