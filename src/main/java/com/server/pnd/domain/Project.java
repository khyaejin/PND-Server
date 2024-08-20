package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repo repo; // 레포지토리 ID

    @NotNull
    @Column(nullable = false)
    private String title; // 프로젝트 이름

    private String period; // 프로젝트 기간

    private String image; // 썸네일

    private String type; // 개발자 유형

    @Column(name = "type_description", nullable = true)
    private String typeDescription; // 개발자 유형 설명

    private String part; //파 (백엔드, 프론트앤드 등)

}
