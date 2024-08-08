package com.server.pnd.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ClassDiagramCreatedRequestDto {
    private Long projectId;
}
