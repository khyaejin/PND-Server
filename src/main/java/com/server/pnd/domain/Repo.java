package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REPO")
public class Repo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "repo_name")
    private String repoName; // 레포지토리 이름

    @Column(name = "repo_url")
    private String repoURL; // 레포지토리 URL

    @Column(name = "repo_description")
    private String repoDescription; // 레포지토리 설명

    @Column(name = "repo_stars")
    private int repoStars; // 레포지토리 스타

    @Column(name = "repo_forks_count")
    private int repoForksCount; // 포크 수

    @Column(name = "repo_openIssues")
    private int repoOpenIssues; // 열려있는 이슈 수

    @Column(name = "repo_watcher")
    private int repoWatcher; // 보고 있는 사용자 수 (업데이트 알람 설정)

    @Column(name = "repo_language")
    private String repoLanguage; // 주 사용 언어

    @Column(name = "repo_disclosure")
    private String repoDisclosure; // 공개여부

    private String title; // 제목

    private String image; // 썸네일

    private String period; // 기간

    @Column(name = "created_at")
    private String createdAt; //레포지토리 생성 일시

    @Column(name = "updated_at")
    private String updatedAt; //레포지토리 최종 수정 일시


    // createdAt을 yyyy.MM.dd 형식으로 변환하여 반환하는 메서드
    public String getFormattedCreatedAt() {
        return formatDateString(createdAt);
    }

    // updatedAt을 yyyy.MM.dd 형식으로 변환하여 반환하는 메서드
    public String getFormattedUpdatedAt() {
        return formatDateString(updatedAt);
    }

    // 날짜 문자열을 형식화하는 헬퍼 메서드
    private String formatDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        try {
            Instant instant = Instant.parse(dateString);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return localDateTime.format(formatter);
        } catch (Exception e) {
            return "";
        }
    }
}
