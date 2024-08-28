package com.server.pnd.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReportDetailResponseDto {
    private Long id;
    private String repoTitle;
    private String image;
    private String createdAt;
}
