package com.server.pnd.repo.controller;

import com.server.pnd.repo.dto.RepoCreatedRequestDto;
import com.server.pnd.repo.service.RepoService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/repo")
public class RepoController {
    private final RepoService repoService;

    // 레포 전체 조회
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchRepoList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return repoService.searchRepoList(authorizationHeader);
    }

    // 레포 기본 정보 세팅
    @PutMapping({"/{repo_id}"})
    public ResponseEntity<CustomApiResponse<?>> settingRepo(
            @PathVariable("repo_id") Long repoId,
            @RequestBody RepoCreatedRequestDto repoCreatedRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile images) throws IOException {
        return repoService.settingRepo(repoId, repoCreatedRequestDto, images);
    }

//    // 레포 상세 조회
//    @GetMapping("/{repo_id}")
//    public ResponseEntity<CustomApiResponse<?>> searchRepoDetail(
//            @PathVariable("repo_id") Long repoId) {
//
//        return repoService.searchRepoDetail(repoId);
//    }
}
