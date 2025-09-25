package com.join.tab.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/test")
public class TestErrorController {

    @GetMapping("/error500")
    public String trigger500() {
        throw new RuntimeException("Simulated 500 error");
    }

    @GetMapping("/error404")
    public String trigger404() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Simulated 404 error");
    }

    @GetMapping("/error400")
    public String trigger400() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Simulated 400 error");
    }

    @GetMapping("/error405")
    public String trigger405() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Simulated 400 error");
    }


}
