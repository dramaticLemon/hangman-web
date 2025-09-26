package com.join.tab.configuration.error;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.validation.BindException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException ex, Model model) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        model.addAttribute("error", ex.getReason() != null ? ex.getReason() : status.getReasonPhrase());

        switch (status) {
            case NOT_FOUND: return "error/404";
            case BAD_REQUEST: return "error/400";
            case METHOD_NOT_ALLOWED: return "error/405";
            default: return "error/500";
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "Page not found: " + ex.getRequestURL());
        return "error/404";
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            BindException.class,
            IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Internal server error");
        return "error/500";
    }
}