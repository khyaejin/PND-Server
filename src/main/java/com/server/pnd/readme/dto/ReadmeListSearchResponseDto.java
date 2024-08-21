package com.server.pnd.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReadmeListSearchResponseDto {
    private Long readmeId;
    private String title;
    private String content;
}
