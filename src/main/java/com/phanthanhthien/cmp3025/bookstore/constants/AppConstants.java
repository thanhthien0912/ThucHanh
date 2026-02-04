package com.phanthanhthien.cmp3025.bookstore.constants;

/**
 * Application Constants
 * Chứa các hằng số dùng chung trong ứng dụng
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
public final class AppConstants {

    private AppConstants() {
        // Private constructor để ngăn khởi tạo
    }

    // Application Info
    public static final String APP_NAME = "Hệ thống Quản lý Nhà sách";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "Phan Thanh Thien";
    public static final String APP_MSSV = "2280603036";
    public static final String APP_COURSE = "CMP3025";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // Validation Messages
    public static final String MSG_REQUIRED = "Trường này là bắt buộc";
    public static final String MSG_INVALID_PRICE = "Giá phải lớn hơn 0";
    public static final String MSG_INVALID_STOCK = "Số lượng phải lớn hơn hoặc bằng 0";

    // Role Names
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // URL Patterns
    public static final String URL_HOME = "/";
    public static final String URL_LOGIN = "/dangnhap";
    public static final String URL_LOGOUT = "/dangxuat";
    public static final String URL_BOOKS = "/sach";
    public static final String URL_CATEGORIES = "/danhmuc";
    public static final String URL_ADMIN = "/quantri";

}
