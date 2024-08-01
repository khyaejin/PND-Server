package com.server.pnd.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepositoryInfoDto {
    private String name; // 레포지토리 이름
    private String htmlUrl; // 레포지토리 url
    private int stars; // 스타
    private String description; // 설명
    private int forksCount; // 포크 수
    private int openIssues; // 현재 열려있는 이슈
    private int watchers; // 지켜보는 사용자 수(업데이트 알람 받는 사람 수)
    private String language; // 주요 사용 언어
    private String createdAt; // 생성 날짜
    private String updatedAt; // 최근 편집 날짜
}
