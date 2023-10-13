package com.example.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The type Root controller.
 */
@Controller
public class RootController {

    /**
     * Root string.
     *
     * @return the string
     */
    @GetMapping("/api")
    public String root() {
        System.out.println("hi");

        return "index";
    }

}
