package com.server.pnd.readme.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ReadmeEditRequestDto {
    private Long readmeId;
    private String content;
}
