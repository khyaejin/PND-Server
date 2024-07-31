package com.server.pnd.user.dto;

import com.server.pnd.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String githubId;
    private String name;
    private String email;
    // @Builder.Default //후에 기본값 지정해주기 -> 기본 카카오톡 프로필?
    private String image;
    private String accessToken;
    private String refreshToken;

    public User toEntity() {
        return User.builder()
                .githubId(githubId)
                .name(name)
                .email(email)
                .image(image)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
