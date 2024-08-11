package com.server.pnd.classDiagram.dto;

import com.server.pnd.domain.Repository;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagramRequestDto {
    private Long repositoryId; // 레포지토리 ID
}
