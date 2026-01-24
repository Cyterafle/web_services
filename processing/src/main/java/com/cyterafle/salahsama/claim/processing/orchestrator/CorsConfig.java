package com.cyterafle.salahsama.claim.processing.orchestrator;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow cross-origin requests to websocket endpoints (SockJS requires credentials and specific origin)
        registry.addMapping("/websocket/**")
                .allowedOrigins("http://localhost:8000", "http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/websocket-raw/**")
                .allowedOrigins("http://localhost:8000", "http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        // Allow all other endpoints for development
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
