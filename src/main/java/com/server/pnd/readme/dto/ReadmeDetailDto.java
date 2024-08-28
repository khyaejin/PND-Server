package com.server.pnd.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReadmeDetailDto {
    private Long id;
    private String readmeTitle;
    private String readmeScript;
    private String createdAt;
}
