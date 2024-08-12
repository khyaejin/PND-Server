package com.server.pnd.gpt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    // RestTemplate 빈을 사용하여 외부 API 호출
    // RestTemplate을 Spring 컨텍스트에 빈으로 등록
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}