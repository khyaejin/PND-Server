package com.server.pnd.repo.service;

import com.server.pnd.repo.dto.RepoSettingRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RepoService {
    // 1. 레포 전체 조회
    ResponseEntity<CustomApiResponse<?>> getAllRepository(String authorizationHeader);
    // 2. 생성된 레포 전체 조회
    ResponseEntity<CustomApiResponse<?>> findReposWithExistingDocuments(String authorizationHeader);
    // 3. 레포 기본 정보 세팅
    ResponseEntity<CustomApiResponse<?>> settingRepo(Long repoId, RepoSettingRequestDto repoSettingRequestDto, MultipartFile images);
    // 4. 레포 정보 수정

    // 5. 레포 갱신

}