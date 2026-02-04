package com.phanthanhthien.cmp3025.bookstore.utils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility Class - Các hàm tiện ích dùng chung
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
public final class AppUtils {

    private AppUtils() {
        // Private constructor để ngăn khởi tạo
    }

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,###");
    private static final DateTimeFormatter DATE_FORMAT = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Format giá tiền sang dạng VNĐ
     */
    public static String formatPrice(java.math.BigDecimal price) {
        if (price == null) return "0";
        return PRICE_FORMAT.format(price) + " VNĐ";
    }

    /**
     * Format LocalDateTime sang chuỗi
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_FORMAT);
    }

    /**
     * Kiểm tra chuỗi có rỗng hoặc null không
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Truncate text nếu quá dài
     */
    public static String truncate(String text, int maxLength) {
        if (isEmpty(text) || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

}
