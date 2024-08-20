package com.server.pnd.diagram.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagramResponseDto {
    private Long diagramId; // 부여되는 다이어그램 ID
    private String flowchart; // Flow Chart 코드를 String 형식으로 전달
}
