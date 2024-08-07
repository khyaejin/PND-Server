package com.server.pnd.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectCreatedRequestDto {
    private Long projectId;
    private Long repositoryId;
    private String period;
    private String image;
    private String part;
}
