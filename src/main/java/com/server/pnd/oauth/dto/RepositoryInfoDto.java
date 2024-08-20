package com.server.pnd.oauth.dto;

import com.server.pnd.domain.Repo;
import com.server.pnd.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private User user;

    public Repo toEntity() {
        return Repo.builder()
                .user(user)
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
