package com.bankapp.configs;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bankapp.constants.Message;

@ControllerAdvice
class ExceptionHandlerConfig {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(HttpSessionRequiredException.class)
    public String handleSessionExpired(HttpServletRequest req, Exception exception, RedirectAttributes attributes) {
        attributes.addFlashAttribute("error", new Message("error", "Your session has expired. Please login again"));
        return "redirect:/";
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        // If the exception is annotated with @ResponseStatus rethrow it and let
        // the framework handle it - like the OrderNotFoundException example
        // at the start of this post.
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        // Otherwise setup and send the user to a default error-view.
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", new Message("error", "Unexpected error occurred."));
        mav.setViewName(DEFAULT_ERROR_VIEW);
        return mav;
    }
}