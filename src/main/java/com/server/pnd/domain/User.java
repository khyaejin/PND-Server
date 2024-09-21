package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "github_id")
    private String githubId;

    private String name;

    @Column(name = "nick_name")
    private String nickName;

    private String image; //프로필 이미지

    private String email;

    @NotEmpty
    @Column(name = "access_token")
    private String accessToken;

    @NotEmpty
    @Column(name = "refresh_token")
    private String refreshToken;

    // 프로필 편집 - 이미지
    public void editUserImage(String imageUrl) {
        this.image = imageUrl;
    }

    // 프로필 편집 - 이미지 제외
    public void editUserWithoutImage(String nickName, String email) {
        this.nickName = nickName;
        this.email = email;
    }
}
