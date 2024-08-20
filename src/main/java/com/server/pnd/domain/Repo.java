package com.server.pnd.domain;

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
@Table(name = "REPOSITORY")
public class Repo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    private String name; // 레포지토리 이름

    private String htmlUrl; // 레포지토리 URL

    private String description; // 레포지토리 설명

    private int stars; // 레포지토리 스타

    private int forksCount; // 포크 수

    private int openIssues; // 열려있는 이슈 수

    private int watchers; // 보고 있는 사용자 수 (업데이트 알람 설정)

    private String language; // 주 사용 언어

    @JoinColumn(name = "created_at")
    private String createdAt; //레포지토리 생성 일시

    @JoinColumn(name = "updated_at")
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
