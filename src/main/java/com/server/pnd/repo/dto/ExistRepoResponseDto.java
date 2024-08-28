package com.server.pnd.repo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExistRepoResponseDto {
    private Long id;
    private String title;
    private String period;
    private String image;
    private boolean isExistReadme;
    private boolean isExistClassDiagram;
    private boolean isExistSequenceDiagram;
    private boolean isExistErDiagram;
    private boolean isExistReport;

}
