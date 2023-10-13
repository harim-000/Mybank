package com.example.backend.controller.member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * The type Profile config.
 */
@Configuration
public class ProfileConfig {

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        File uploadFolder = new File(uploadPath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
    }
}