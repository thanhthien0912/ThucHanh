package com.phanthanhthien.cmp3025.bookstore.controller;

import com.phanthanhthien.cmp3025.bookstore.entities.Voucher;
import com.phanthanhthien.cmp3025.bookstore.services.VoucherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AdminVoucherController - Quản lý Voucher cho Admin
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/quantri/voucher")
public class AdminVoucherController {

    @Autowired
    private VoucherService voucherService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping({ "", "/" })
    public String listVouchers(Model model) {
        log.info("Loading all vouchers...");
        List<Voucher> vouchers = voucherService.findAll();
        log.info("Found {} vouchers", vouchers.size());

        model.addAttribute("pageTitle", "Quản lý Voucher");
        model.addAttribute("currentPage", "voucher");
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("totalVouchers", voucherService.count());

        return "voucher/index";
    }

    @GetMapping("/them")
    public String addVoucherForm(Model model) {
        model.addAttribute("pageTitle", "Thêm Voucher mới");
        model.addAttribute("currentPage", "voucher");

        Voucher voucher = new Voucher();
        voucher.setDiscountPercent(java.math.BigDecimal.valueOf(10));
        voucher.setMaxUsage(1000);
        voucher.setIsActive(true);
        voucher.setValidFrom(java.time.LocalDateTime.now().plusMinutes(5));
        voucher.setValidTo(java.time.LocalDateTime.now().plusMonths(6));

        model.addAttribute("voucher", voucher);
        return "voucher/form";
    }

    @PostMapping("/them")
    public String addVoucher(
            @Valid @ModelAttribute("voucher") Voucher voucher,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Creating voucher with code: {}", voucher.getCode());

        if (result.hasErrors()) {
            log.error("Validation errors: {}", result.getAllErrors());
            model.addAttribute("pageTitle", "Thêm Voucher mới");
            model.addAttribute("currentPage", "voucher");
            return "voucher/form";
        }

        try {
            log.info("Voucher data - code: {}, discount: {}, validFrom: {}, validTo: {}",
                    voucher.getCode(), voucher.getDiscountPercent(), voucher.getValidFrom(), voucher.getValidTo());

            voucherService.create(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm voucher mới thành công!");
            return "redirect:/quantri/voucher";
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Thêm Voucher mới");
            model.addAttribute("currentPage", "voucher");
            return "voucher/form";
        } catch (Exception e) {
            log.error("Error creating voucher: ", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("pageTitle", "Thêm Voucher mới");
            model.addAttribute("currentPage", "voucher");
            return "voucher/form";
        }
    }

    @GetMapping("/sua/{id}")
    public String editVoucherForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Voucher> voucher = voucherService.findById(id);

        if (voucher.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy voucher!");
            return "redirect:/quantri/voucher";
        }

        model.addAttribute("pageTitle", "Cập nhật Voucher");
        model.addAttribute("currentPage", "voucher");
        model.addAttribute("voucher", voucher.get());
        model.addAttribute("isEdit", true);
        return "voucher/form";
    }

    @PostMapping("/sua/{id}")
    public String updateVoucher(
            @PathVariable String id,
            @Valid @ModelAttribute("voucher") Voucher voucher,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật Voucher");
            model.addAttribute("currentPage", "voucher");
            model.addAttribute("isEdit", true);
            return "voucher/form";
        }

        try {
            voucherService.update(id, voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật voucher thành công!");
            return "redirect:/quantri/voucher";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Cập nhật Voucher");
            model.addAttribute("currentPage", "voucher");
            model.addAttribute("isEdit", true);
            return "voucher/form";
        } catch (Exception e) {
            log.error("Error updating voucher: {}", e.getMessage());
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("pageTitle", "Cập nhật Voucher");
            model.addAttribute("currentPage", "voucher");
            model.addAttribute("isEdit", true);
            return "voucher/form";
        }
    }

    @GetMapping("/xoa/{id}")
    public String deleteVoucher(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            voucherService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa voucher thành công!");
        } catch (Exception e) {
            log.error("Error deleting voucher: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa voucher!");
        }
        return "redirect:/quantri/voucher";
    }

    @PostMapping("/toggle-status/{id}")
    @ResponseBody
    public Map<String, Object> toggleStatus(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Voucher> voucherOpt = voucherService.findById(id);
            if (voucherOpt.isPresent()) {
                Voucher voucher = voucherOpt.get();
                voucher.setIsActive(!voucher.getIsActive());
                voucherService.update(id, voucher);
                response.put("success", true);
                response.put("isActive", voucher.getIsActive());
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy voucher");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
}
