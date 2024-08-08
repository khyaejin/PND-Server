package com.server.pnd.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectSearchDetailResponseDto {
    private String title;
    private String period;
    private String createdAt;
    private String image;
    private String classDiagram;
}
