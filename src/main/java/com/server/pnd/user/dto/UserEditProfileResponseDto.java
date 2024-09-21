package com.server.pnd.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserEditProfileResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String image;
}
