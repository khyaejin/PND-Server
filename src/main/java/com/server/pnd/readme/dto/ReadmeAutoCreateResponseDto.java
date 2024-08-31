package com.server.pnd.readme.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadmeAutoCreateResponseDto {
    private String readme_script_gpt; // GPT 리드미 스크립트
}
