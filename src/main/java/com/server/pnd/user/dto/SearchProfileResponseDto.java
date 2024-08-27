package com.server.pnd.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class SearchProfileResponseDto {
    private String name;
    private String image; // 깃허브 프로필
    private String email;
    private int totalDocs; // 생성한 문서 총 개수
    private int totalReadmes; // 생성한 리드미 총 개수
    private int totalDiagrams; // 생성한 다이어그램 총 개수
    private int totalReports; // 생성한 깃허브 레포트 총 개수
}
