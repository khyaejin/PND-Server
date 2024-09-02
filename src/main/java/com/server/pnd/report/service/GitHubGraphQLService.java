package com.server.pnd.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GitHubGraphQLService {

    private final RestTemplate restTemplate;

    public String fetchUserData(String accessToken, String username, String repositoryName) {
        String url = "https://api.github.com/graphql";

        // GraphQL 쿼리 정의
        String query = "{ \"query\": \"query { repository(owner: \\\"" + username + "\\\", name: \\\"" + repositoryName + "\\\") { "
                + "name "
                + "forkCount "
                + "stargazerCount "
                + "primaryLanguage { name color } "
                + "defaultBranchRef { "
                + "target { "
                + "... on Commit { "
                + "history(first: 100) { "
                + "edges { "
                + "node { "
                + "committedDate "
                + "additions "
                + "deletions "
                + "changedFiles "
                + "author { name } "
                + "} "
                + "} "
                + "} "
                + "} "
                + "} "
                + "} "
                + "languages(first: 10) { "
                + "edges { "
                + "node { name color } "
                + "size "
                + "} "
                + "} "
                + "} "
                + "}\" }";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        // 요청 보내기
        HttpEntity<String> entity = new HttpEntity<>(query, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 응답 본문 반환
        return response.getBody();
    }

}
