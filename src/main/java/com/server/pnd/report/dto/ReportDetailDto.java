package com.server.pnd.report.dto;

import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReportDetailDto {
    private Long id;
    private String repoTitle;
    private String createdAt;
    private String imageGreen; // 이미지 URL
    private String imageSeason; // 이미지 URL
    private String imageSouthSeason; // 이미지 URL
    private String imageNightView; // 이미지 URL
    private String imageNightGreen; // 이미지 URL
    private String imageNightRainbow; // 이미지 URL
    private String imageGitblock; // 이미지 URL

}
