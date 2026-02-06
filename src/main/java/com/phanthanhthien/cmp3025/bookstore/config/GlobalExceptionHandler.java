package com.phanthanhthien.cmp3025.bookstore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * GlobalExceptionHandler - Xử lý exception toàn cục
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("Exception occurred: ", e);

        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        mav.setViewName("error/500");
        return mav;
    }
}
