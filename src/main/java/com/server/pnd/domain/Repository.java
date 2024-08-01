package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REPOSITORY")
public class Repository extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty @Column(name = "user_id")
    private Long userId;

    @NotEmpty
    private String name; // 레포지토리 이름

    @NotEmpty
    private String url; // 레포지토리 URL

    private String description; // 레포지토리 설명

    private int stars; // 레포지토리 스타

    private int forksCount; // 포크 수

    private int openIssues; // 열려있는 이슈 수

    private int watchers; // 보고 있는 사용자 수 (업데이트 알람 받음)

    private String language; // 주 사용 언어

}
