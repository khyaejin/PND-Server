package com.server.pnd.util.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value; // * lombok이 아닌 annotation의 Value 사용
import org.springframework.context.annotation.Configuration;

// 구성 요소

@Getter
@Configuration
public class ChatGPTConfig {
    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl; // gpt api 엔드포인트에 해당
}
