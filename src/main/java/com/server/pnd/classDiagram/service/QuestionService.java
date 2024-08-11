package com.server.pnd.classDiagram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.pnd.classDiagram.gpt.config.ChatGPTConfig;
import com.server.pnd.classDiagram.gpt.dto.ChatCompletionDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {
    private final ChatGPTConfig chatGPTConfig;
    private final ObjectMapper objectMapper; // JSON 데이터를 처리하기 위해 사용되는 Jackson 라이브러리의 ObjectMapper 객체

    // ChatCompletionDto 객체를 받아서, 이를 GPT API에 요청하는 메서드
    public String callGptApi(ChatCompletionDto chatCompletionDto) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // JSON 요청 본문 생성
            Map<String, Object> requesetBody = chatCompletionDto.toRequestBody();
            String requestBodyJson = objectMapper.writeValueAsString(requesetBody); // JSON 형식으로 직렬화

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(chatGPTConfig.getApiUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + chatGPTConfig.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 형태 확인
            System.out.println("GPT 응답 상태 코드: " + response.statusCode());
            System.out.println("GPT 응답 본문: " + response.body());

            if(response.statusCode() == 200) {
                // 성공적인 응답은 JSON 파싱을 통해 내용을 추출
                JsonNode jsonNode = objectMapper.readTree(response.body());
                return jsonNode.path("choices").get(0).path("message").path("content").asText();
            } else if(response.statusCode() == 400) {
                // 잘못된 요청에 대한 실패 메시지 반환
                return "응답 생성에 실패했습니다. 잘못된 요청입니다.";
            } else {
                // 기타 오류에 대한 처리
                return "응답 생성 중 오류가 발생했습니다. 상태 코드: " + response.statusCode();
            }


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
