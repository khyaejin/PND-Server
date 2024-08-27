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
@Table(name = "DIAGRAM")
public class Diagram extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull
    @JoinColumn(name = "repo_id")
    private Repo repo;

    // 클래스 다이어그램
    @Column(columnDefinition = "TEXT", name = "class_script")
    private String classScript;
    @Column(columnDefinition = "TEXT", name = "class_script_gpt")
    private String classScriptGpt;

    // 시퀀스 다이어그램
    @Column(columnDefinition = "TEXT", name = "sequence_script")
    private String sequenceScript  ;
    @Column(columnDefinition = "TEXT", name = "sequence_script_gpt")
    private String sequenceScriptGpt;

    // ERD
    @Column(columnDefinition = "TEXT", name = "erd_script")
    private String erdScript;
    @Column(columnDefinition = "TEXT", name = "erd_script_gpt")
    private String erdScriptGpt;

    // classScriptGpt 필드를 설정하는 메서드
    public void updateClassScriptGpt(String classScriptGpt) {
        this.classScriptGpt = classScriptGpt;
    }

    // sequenceScriptGpt 필드를 설정하는 메서드
    public void updateSequenceScriptGpt(String sequenceScriptGpt) {
        this.sequenceScriptGpt = sequenceScriptGpt;
    }
}
