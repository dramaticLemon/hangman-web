package com.join.tab.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.join.tab.services.WordService;

@Controller
public class HelloController {

    private final WordService service;

    public HelloController (WordService service) {
        this.service = service;
    }

    @GetMapping
    public String helloMethod(Model model) {
        model.addAttribute("word", service.getWord());
        return "index";
    }
}
