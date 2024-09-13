package com.server.pnd.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CreateReportResponseDto {
    private Long id; // 리포트 고유 ID
    private String repoTitle; //레포트 제목은 없음. 레포의 제목
    private String imageGreen; // 이미지 URL
    private String imageSeason; // 이미지 URL
    private String imageSouthSeason; // 이미지 URL
    private String imageNightView; // 이미지 URL
    private String imageNightGreen; // 이미지 URL
    private String imageNightRainbow; // 이미지 URL
    private String imageGitblock; // 이미지 URL
    private String createdAt; //생성일자
}
