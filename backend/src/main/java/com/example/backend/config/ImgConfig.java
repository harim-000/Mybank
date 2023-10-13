package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class ImgConfig implements WebMvcConfigurer {
    private final String profilePath = "/Users/ta9/ojt_project/frontend/src/assets/profile/";
    private final String noticePath = "/Users/ta9/ojt_project/frontend/src/assets/noticefile/";
    // 프로필 이미지 경로를 정적 자원으로 인식해 접근하도록 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/profile/**")
                .addResourceLocations("file:" + profilePath)
                .setCachePeriod(30);
        registry.addResourceHandler("/assets/noticefile/**")
                .addResourceLocations("file:" + noticePath)
                .setCachePeriod(30);
    }
}
