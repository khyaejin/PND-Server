package com.server.pnd.test.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
@Builder
public class ClassDiagramCreatedRequestDto {
    private Long projectId;
}
