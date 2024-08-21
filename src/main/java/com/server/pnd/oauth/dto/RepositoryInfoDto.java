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
    private String repoName;
    private String repoUrl;
    private int repoStars;
    private String repoDescription;
    private int repoForksCount;
    private int repoOpenIssues;
    private int repoWatcher;
    private String repoLanguage;
    private String createdAt;
    private String updatedAt;
    private User user;

    public Repo toEntity() {
        return Repo.builder()
                .user(user)
                .repoName(repoName)
                .repoURL(repoUrl)
                .repoStars(repoStars)
                .repoDescription(repoDescription)
                .repoForksCount(repoForksCount)
                .repoOpenIssues(repoOpenIssues)
                .repoLanguage(repoLanguage)
                .repoWatcher(repoWatcher)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
