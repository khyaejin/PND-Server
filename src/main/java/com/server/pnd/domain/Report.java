package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REPORT")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty @Column(name = "project_id")
    private Long projectId;

    @NotEmpty
    private String description; // 한줄 요약 설명

    @NotEmpty
    private String spec; // 사용 스펙

    private String accomplishment; // 성과

    private String weakness; // 문제점 및 문제 해결

    private String strength; // 잘된 점

    private String improvement; // 개선할 점

    private String recommendations; // 학습 추천

    @Column(name = "custom_question")
    private String customQuestion; // 프로젝트 맞춤 질문

    @Column(name = "image_link")
    private String imageLink; // 이미지 URL

}
