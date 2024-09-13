package com.server.pnd.report.dto;

import lombok.Builder;


@Builder
public class CreateReportResponseDto {
    private Long id; // 리포트 고유 ID
    private String repoTitle; //레포트 제목은 없음. 레포의 제목
    private String image; //리포트 이미지 배포 url
    private String createdAt; //생성일자

}
