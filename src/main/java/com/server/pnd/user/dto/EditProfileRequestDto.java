package com.server.pnd.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class EditProfileRequestDto {
    private String name;
    private String email;
}
