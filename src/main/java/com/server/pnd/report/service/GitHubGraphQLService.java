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

    public String fetchUserData(String accessToken, String username, String organizationName,  String repositoryName) {
        String url = "https://api.github.com/graphql";

        // 조직 레포지토리일 경우 organizationName을 사용, 아니면 username 사용
        String ownerName = !organizationName.isEmpty() ? organizationName : username;

        // GraphQL 쿼리 정의
        String query = "{ \"query\": \"query { repository(owner: \\\"" + ownerName + "\\\", name: \\\"" + repositoryName + "\\\") { "
                + "name "
                + "forkCount "
                + "stargazerCount "
                + "primaryLanguage { name color } "
                + "defaultBranchRef { "
                + "name "  // 브랜치 이름을 확인하기 위해 추가
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

        System.out.println("GraphQL Query Response: " + response.getBody());

        // 응답 본문 반환
        return response.getBody();
    }

}
