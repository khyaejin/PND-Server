package com.server.pnd.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ProjectSearchListResponseDto {
    private String image; // 썸네일
    private String title; // 제목
}
