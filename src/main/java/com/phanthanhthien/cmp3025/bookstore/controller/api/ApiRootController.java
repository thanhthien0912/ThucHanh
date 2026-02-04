package com.phanthanhthien.cmp3025.bookstore.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ApiRootController - Root API endpoint
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
public class ApiRootController {

    /**
     * GET /api - Thông tin API
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Bookstore Management API");
        info.put("version", "1.0.0");
        info.put("author", "Phan Thanh Thien - MSSV: 2280603036");
        info.put("course", "CMP3025 - J2EE");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("books", "/api/v1/books");
        endpoints.put("categories", "/api/v1/categories");
        
        info.put("endpoints", endpoints);
        info.put("status", "active");
        
        return ResponseEntity.ok(info);
    }

}
