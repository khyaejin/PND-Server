package com.server.pnd.diagram.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagramUpdateRequestDto {
    private Long repoId; // 레포지토리 ID
    private String script; // 다이어그램 스크립트
}
