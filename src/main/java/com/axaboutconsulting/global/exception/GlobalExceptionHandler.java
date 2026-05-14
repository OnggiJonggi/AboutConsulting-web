package com.axaboutconsulting.global.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public String handleCustomException(CustomException e, Model model) {
        model.addAttribute("message", e.getErrorCode().getMessage());
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error/500";
    }
}
