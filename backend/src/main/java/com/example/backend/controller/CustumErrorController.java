package com.example.backend.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The type Custum error controller.
 */
@Controller
public class CustumErrorController implements ErrorController {

    /**
     * Redirect root string.
     *
     * @return the string
     */
    @GetMapping("/error")
    public String redirectRoot() {
        return "/index";
    }

    /**
     * Gets error path.
     *
     * @return the error path
     */
    public String getErrorPath() {
        return null;
    }
}
