package com.server.pnd.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ReadmeSavedRequestDto {
    private Long repoId;
    private String content;
}
