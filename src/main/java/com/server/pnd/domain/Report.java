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

    @JoinColumn(name = "image_gitblock")
    private String imageGitblock; // 이미지 URL

    @JoinColumn(name = "image_green")
    private String imageGreen; // 이미지 URL

    @JoinColumn(name = "image_night_green")
    private String imageNightGreen; // 이미지 URL

    @JoinColumn(name = "image_night_rainbow")
    private String imageNightRainbow; // 이미지 URL

    @JoinColumn(name = "image_night_view")
    private String imageNightView; // 이미지 URL

    @JoinColumn(name = "image_season")
    private String imageSeason; // 이미지 URL

    @JoinColumn(name = "image_south_season")
    private String imageSouthSeason; // 이미지 URL

}
