package com.server.pnd.markdown.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MarkdownSavedResponseDto {
    private Long markdownId;
}
