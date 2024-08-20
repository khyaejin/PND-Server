package com.server.pnd.repo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepoSearchDetailResponseDto {
    private String title;
    private String period;
    private String createdAt;
    private String image;
    private String classDiagram;
}
