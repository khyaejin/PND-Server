package com.server.pnd.repo.service;

import com.server.pnd.repo.dto.RepoCreatedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface RepoService {
    // 프로젝트 생성
    ResponseEntity<CustomApiResponse<?>> createRepo(String authorizationHeader, RepoCreatedRequestDto repoCreatedRequestDto);

    // 프로젝트 전체 조회
    ResponseEntity<CustomApiResponse<?>> searchRepoList(String authorizationHeader);

    // 프로젝트 상세 조회
    ResponseEntity<CustomApiResponse<?>> searchRepoDetail(Long repoId);
}