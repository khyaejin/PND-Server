package com.server.pnd.user.dto;

import jakarta.persistence.JoinColumn;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@Builder
public class RepositorySearchResponseDto {
    private Long id;

    private String name; // 레포지토리 이름

    private String description; // 레포지토리 설명

    private int stars; // 레포지토리 스타

    private int forksCount; // 포크 수

    private int openIssues; // 열려있는 이슈 수

    private int watchers; // 보고 있는 사용자 수 (업데이트 알람 받음)

    private String language; // 주 사용 언어

    private String createdAt; //레포지토리 생성 일시
}
