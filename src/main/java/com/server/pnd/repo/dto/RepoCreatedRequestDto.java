package com.server.pnd.repo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepoCreatedRequestDto {
    private Long repoId;
    private String period;
    private String image;
    private String part;
    private String title;
}
