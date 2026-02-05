package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Category;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
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
}
