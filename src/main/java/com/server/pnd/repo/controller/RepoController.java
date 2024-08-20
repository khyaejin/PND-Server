package com.server.pnd.repo.controller;

import com.server.pnd.repo.dto.RepoCreatedRequestDto;
import com.server.pnd.repo.service.RepoService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/repo")
public class RepoController {
    private final RepoService repoService;
    // 레포지토리 생성
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> createRepo(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody RepoCreatedRequestDto repoCreatedRequestDto) {
        return repoService.createRepo(authorizationHeader, repoCreatedRequestDto);
    }

    // 레포지토리 전체 조회
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchRepoList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return repoService.searchRepoList(authorizationHeader);
    }

    // 레포지토리 상세 조회
    @GetMapping("/{repo_id}")
    public ResponseEntity<CustomApiResponse<?>> searchRepoDetail(
            @PathVariable("repo_id") Long repoId){
        return repoService.searchRepoDetail(repoId);
    }
}
