package com.server.pnd.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class EditProfileRequestDto {
    private String nickName;
    private String email;
}
