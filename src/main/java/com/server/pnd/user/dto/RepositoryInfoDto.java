package com.server.pnd.user.dto;

import com.server.pnd.domain.Repository;
import com.server.pnd.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepositoryInfoDto {
    private String name;
    private String htmlUrl;
    private int stars;
    private String description;
    private int forksCount;
    private int openIssues;
    private int watchers;
    private String language;
    private String createdAt;
    private String updatedAt;

    public Repository toEntity() {
        return Repository.builder()
                .name(name)
                .htmlUrl(htmlUrl)
                .stars(stars)
                .description(description)
                .forksCount(forksCount)
                .openIssues(openIssues)
                .language(language)
                .watchers(watchers)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
