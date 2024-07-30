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
@Table(name = "Project")
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotEmpty @Column(name = "id")
    private Long id;

    @Column(name = "repository_id")
    private Long repositoryId; // 레포지토리 ID

    @Column(name = "period")
    private String period; // 프로젝트 기간

    @Column(name = "image")
    private String image; // 썸네일

    @NotEmpty @Column(name = "pbti")
    private String pbti; // 프로젝트 mbti

    @NotEmpty @Column(name = "pbti_description")
    private String pbtiDescription; // 프로젝트 mbti 설명

}
