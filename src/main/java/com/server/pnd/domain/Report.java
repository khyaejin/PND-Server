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

    @OneToOne
    @NotEmpty @JoinColumn(name = "repo_id")
    private Repo repo;

    private String title;

    @JoinColumn(name = "image")
    private String image; // 이미지 URL
}
