package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty @Column(name = "github_id")
    private String githubId;

    private String name;

    private String image; //프로필 이미지

    private String email;

    @NotEmpty @Column(name = "access_token")
    private String accessToken;

    @NotEmpty @Column(name = "refresh_token")
    private String refreshToken;
}
