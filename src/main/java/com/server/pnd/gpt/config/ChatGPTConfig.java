package com.server.pnd.gpt.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value; // * lombok이 아닌 annotation의 Value 사용
import org.springframework.context.annotation.Configuration;

// GPT API의 URL 및 API 키 등 구성을 관리하는 클래스

@Getter
@Configuration
public class ChatGPTConfig {
    @Value("${OPEN_AI_KEY}")
    private String apiKey;

    @Value("${OPEN_AI_URL}")
    private String apiUrl; // gpt api 엔드포인트에 해당
}
