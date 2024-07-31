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
@Table(name = "USER")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotEmpty @Column(name = "id")
    private Long id;

    @NotEmpty @Column(name = "github_id")
    private String githubId;

    private String name;

    private String image;

    private String email;

    @NotEmpty @Column(name = "access_token")
    private String accessToken;

    @NotEmpty @Column(name = "refresh_token")
    private String refreshToken;
}
