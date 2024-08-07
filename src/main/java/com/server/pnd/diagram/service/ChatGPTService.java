package com.server.pnd.diagram.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatGPTService {

    // OpenAI API URL과 API 키를 애플리케이션 properties(key는 application-secret.properties)에서 가져옴
    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Value("${OPEN_AI_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // RestTemplate을 주입받음
    public ChatGPTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ChatGPT API에 요청을 보내고 응답을 받는 메서드
    public Map<String, String> getChatGPTResponses(Map<String, String> prompts) {
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");  // 모델을 gpt-4로 설정
        requestBody.put("prompt", String.join("\n", prompts.values()));
        requestBody.put("max_tokens", 150);

        // 요청 엔티티 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // API 호출 및 응답 받기
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

        // 응답에서 결과 추출
        Map<String, String> result = new HashMap<>();
        result.put("response", response.getBody().toString());  // 응답 내용을 적절히 파싱하여 반환

        return result;
    }
}