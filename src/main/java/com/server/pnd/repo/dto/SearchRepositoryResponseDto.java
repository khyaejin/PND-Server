package com.server.pnd.repo.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@Builder
public class SearchRepositoryResponseDto {
    private Long id;

    private String repoName; // 레포지토리 이름

    private String repoDescription; // 레포지토리 설명

    private int repoStars; // 레포지토리 스타

    private int repoForksCount; // 포크 수

    private int repoOpenIssues; // 열려있는 이슈 수

    private int repoWatcher; // 보고 있는 사용자 수 (업데이트 알람 받음)

    private String repoLanguage; // 주 사용 언어

    private String repoDisclosure;// 공개 여부

    private String createdAt; //레포지토리 생성 일시
}
