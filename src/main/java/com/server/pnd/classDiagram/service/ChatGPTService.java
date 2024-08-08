package com.server.pnd.diagram.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
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
    public String getChatGPTResponse(String prompt) {
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // 요청 본문 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");  // 모델을 gpt-4로 설정
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 150);

        // 요청 엔티티 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // API 호출 및 응답 받기
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

            // 응답에서 결과 추출
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    return (String) choices.get(0).get("text");
                }
            }
            throw new IllegalStateException("ChatGPT API로부터 예상치 못한 응답 구조를 받았습니다.");
        } catch (RestClientException e) {
            throw new RuntimeException("ChatGPT API와의 통신에 실패했습니다.", e);
        }
    }
}
