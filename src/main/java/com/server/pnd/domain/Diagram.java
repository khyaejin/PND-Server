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

    @Column(columnDefinition = "TEXT")
    private String class_script;
    @Column(columnDefinition = "TEXT")
    private String class_script_gpt;

    @Column(columnDefinition = "TEXT")
    private String sequence_script  ;
    @Column(columnDefinition = "TEXT")
    private String sequence_script_gpt;

    @Column(columnDefinition = "TEXT")
    private String erd_script;
    @Column(columnDefinition = "TEXT")
    private String erd_script_gpt;
}
