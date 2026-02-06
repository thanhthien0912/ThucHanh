package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.entities.Order;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.OrderRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service xuất dữ liệu ra file Excel
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Service
public class ExcelExportService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Xuất toàn bộ sách ra file Excel
     */
    public byte[] exportBooksToExcel() throws IOException {
        List<Book> books = bookRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách sách");

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style cho data
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Style cho tiền
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0\" VNĐ\""));

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Tiêu đề", "Tác giả", "Danh mục", "Giá", "Tồn kho", "Ngày tạo" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);

                // ID
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(book.getId());
                cell0.setCellStyle(dataStyle);

                // Tiêu đề
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(book.getTitle());
                cell1.setCellStyle(dataStyle);

                // Tác giả
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(book.getAuthor() != null ? book.getAuthor() : "");
                cell2.setCellStyle(dataStyle);

                // Danh mục
                Cell cell3 = row.createCell(3);
                String categoryName = getCategoryName(book.getCategoryId());
                cell3.setCellValue(categoryName);
                cell3.setCellStyle(dataStyle);

                // Giá
                Cell cell4 = row.createCell(4);
                if (book.getPrice() != null) {
                    cell4.setCellValue(book.getPrice().doubleValue());
                }
                cell4.setCellStyle(moneyStyle);

                // Tồn kho
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(book.getStock() != null ? book.getStock() : 0);
                cell5.setCellStyle(dataStyle);

                // Ngày tạo
                Cell cell6 = row.createCell(6);
                if (book.getCreatedAt() != null) {
                    cell6.setCellValue(book.getCreatedAt().format(DATE_FORMATTER));
                }
                cell6.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi ra byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null)
            return "";
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.map(Category::getName).orElse("");
    }

    /**
     * Xuất toàn bộ đơn hàng ra file Excel
     */
    public byte[] exportOrdersToExcel() throws IOException {
        List<Order> orders = orderRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách đơn hàng");

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style cho data
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Style cho tiền
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0\" VNĐ\""));

            // Style cho trạng thái
            CellStyle successStyle = workbook.createCellStyle();
            successStyle.cloneStyleFrom(dataStyle);
            successStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            successStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle pendingStyle = workbook.createCellStyle();
            pendingStyle.cloneStyleFrom(dataStyle);
            pendingStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            pendingStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle failedStyle = workbook.createCellStyle();
            failedStyle.cloneStyleFrom(dataStyle);
            failedStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            failedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] headers = { "Mã đơn hàng", "Người nhận", "SĐT", "Địa chỉ", "Phương thức thanh toán",
                    "Trạng thái thanh toán", "Trạng thái đơn hàng", "Tổng tiền", "Giảm giá", "Thành tiền",
                    "Ngày đặt", "Ngày thanh toán" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);

                // Mã đơn hàng
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(order.getId());
                cell0.setCellStyle(dataStyle);

                // Người nhận
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(order.getReceiverName() != null ? order.getReceiverName() : "");
                cell1.setCellStyle(dataStyle);

                // SĐT
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(order.getReceiverPhone() != null ? order.getReceiverPhone() : "");
                cell2.setCellStyle(dataStyle);

                // Địa chỉ
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(order.getReceiverAddress() != null ? order.getReceiverAddress() : "");
                cell3.setCellStyle(dataStyle);

                // Phương thức thanh toán
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(order.getPaymentMethod() != null ? order.getPaymentMethod() : "");
                cell4.setCellStyle(dataStyle);

                // Trạng thái thanh toán
                Cell cell5 = row.createCell(5);
                String paymentStatus = order.getPaymentStatus() != null ? order.getPaymentStatus() : "";
                cell5.setCellValue(getPaymentStatusLabel(paymentStatus));
                cell5.setCellStyle(getPaymentStatusCellStyle(paymentStatus, successStyle, pendingStyle, failedStyle));

                // Trạng thái đơn hàng
                Cell cell6 = row.createCell(6);
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus() : "";
                cell6.setCellValue(getOrderStatusLabel(orderStatus));
                cell6.setCellStyle(dataStyle);

                // Tổng tiền
                Cell cell7 = row.createCell(7);
                if (order.getTotalAmount() != null) {
                    cell7.setCellValue(order.getTotalAmount().doubleValue());
                }
                cell7.setCellStyle(moneyStyle);

                // Giảm giá
                Cell cell8 = row.createCell(8);
                if (order.getDiscountAmount() != null) {
                    cell8.setCellValue(order.getDiscountAmount().doubleValue());
                }
                cell8.setCellStyle(moneyStyle);

                // Thành tiền
                Cell cell9 = row.createCell(9);
                if (order.getFinalAmount() != null) {
                    cell9.setCellValue(order.getFinalAmount().doubleValue());
                }
                cell9.setCellStyle(moneyStyle);

                // Ngày đặt
                Cell cell10 = row.createCell(10);
                if (order.getCreatedAt() != null) {
                    cell10.setCellValue(order.getCreatedAt().format(DATE_FORMATTER));
                }
                cell10.setCellStyle(dataStyle);

                // Ngày thanh toán
                Cell cell11 = row.createCell(11);
                if (order.getPaidAt() != null) {
                    cell11.setCellValue(order.getPaidAt().format(DATE_FORMATTER));
                }
                cell11.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi ra byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String getPaymentStatusLabel(String status) {
        if (status == null) return "";
        return switch (status) {
            case "SUCCESS" -> "Thành công";
            case "PENDING" -> "Chờ thanh toán";
            case "FAILED" -> "Thất bại";
            default -> status;
        };
    }

    private String getOrderStatusLabel(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PENDING" -> "Chờ xử lý";
            case "PROCESSING" -> "Đang xử lý";
            case "SHIPPED" -> "Đang giao";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private CellStyle getPaymentStatusCellStyle(String status, CellStyle successStyle,
                                              CellStyle pendingStyle, CellStyle failedStyle) {
        if (status == null) return successStyle; // Default
        return switch (status) {
            case "SUCCESS" -> successStyle;
            case "PENDING" -> pendingStyle;
            case "FAILED" -> failedStyle;
            default -> successStyle;
        };
    }
}
