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
@Table(name = "README")
public class Readme extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "repo_id")
    private Repo repo;

    @Column(columnDefinition = "LONGTEXT", name = "readme_script")
    private String readme_script;

    @Column(columnDefinition = "LONGTEXT", name = "readme_script_gpt")
    private String readme_script_gpt;

    // 제목 설정
    public void setContent(String content) {
        this.readme_script = content;
    }

    // 리드미 자동 생생 스크립트 저장
    public void setReadmeScriptGpt(String readme_script_gpt) {
        this.readme_script_gpt = readme_script_gpt;
    }
}
