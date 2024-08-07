package com.server.pnd.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class SearchProfileResponseDto {
    private String name;
    private String image; // 깃허브 프로필
    private String email;
}
