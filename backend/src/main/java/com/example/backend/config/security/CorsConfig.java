package com.example.backend.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * The type Cors config.
 */
// cors 공격 관련 필터
@Configuration
@ConfigurationProperties(prefix = "frontend")
public class CorsConfig {

    private String url;

    /**
     * Cors filter.
     *
     * @return the cors filter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(url);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/api", configuration);

        return new CorsFilter(source);
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}