package com.server.pnd.util.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class CorsConfig implements WebMvcConfigurer {

    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(DEVELOP_FRONT_ADDRESS) // 소셜로그인 시 리다이렉트되는 포트 번호에서 API 요청 허용
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
