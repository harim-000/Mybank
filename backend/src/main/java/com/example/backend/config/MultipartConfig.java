package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * The type Multipart config.
 */
@Configuration
public class MultipartConfig {

    /**
     * Multipart resolver multipart resolver.
     *
     * @return the multipart resolver
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
