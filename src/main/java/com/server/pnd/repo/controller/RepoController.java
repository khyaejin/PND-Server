package com.server.pnd.repo.controller;

import com.server.pnd.repo.dto.RepoSettingRequestDto;
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
    @GetMapping()
    public  ResponseEntity<CustomApiResponse<?>> getRepositoryList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return repoService.getAllRepository(authorizationHeader);
    }

    // 문서가 생성된 레포 전체 조회
//    @GetMapping
//    public ResponseEntity<CustomApiResponse<?>> searchRepoList(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
//        return repoService.searchRepoList(authorizationHeader);
//    }

    // 레포 기본 정보 세팅
    @PutMapping({"/{repo_id}"})
    public ResponseEntity<CustomApiResponse<?>> settingRepo(
            @PathVariable("repo_id") Long repoId,
            @RequestPart("data") RepoSettingRequestDto repoSettingRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile images) throws IOException {
        return repoService.settingRepo(repoId, repoSettingRequestDto, images);
    }

}
