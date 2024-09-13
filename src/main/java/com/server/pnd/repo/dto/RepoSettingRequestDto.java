package com.server.pnd.repo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepoSettingRequestDto {
    private String period;
    private String title;
}
