package com.server.pnd.gpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값을 가지는 필드는 JSON 직렬화에서 제외
public class ChatCompletionDto {
    private String model; // GPT 모델 종류

    // messages 필드를 JSON 직렬화 시 'messages'라는 이름으로 매핑
    @JsonProperty("messages") // 직렬화
    private List<ChatRequestMsgDto> messages; // 대화 메시지 리스트를 저장

    // JSON 요청 본문 생성을 위한 메서드
    public Map<String, Object> toRequestBody() {
        return Map.of(
                "model", this.model,
                "messages", this.messages
        );
    }
}
