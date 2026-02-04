package com.phanthanhthien.cmp3025.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * OrderController - Mua hàng (Placeholder)
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Controller
@RequestMapping("/muahang")
public class OrderController {

    @GetMapping({"", "/"})
    public String cart(Model model) {
        model.addAttribute("pageTitle", "Giỏ hàng");
        model.addAttribute("currentPage", "muahang");
        return "muahang/index";
    }

}
