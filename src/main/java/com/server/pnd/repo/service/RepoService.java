package com.server.pnd.repo.service;

import com.server.pnd.repo.dto.RepoSettingRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RepoService {
    // 레포 전체 조회
    ResponseEntity<CustomApiResponse<?>> searchRepoList(String authorizationHeader);
    // 생성된 레포 전체 조회

    // 레포 기본 정보 세팅
    ResponseEntity<CustomApiResponse<?>> settingRepo(Long repoId, RepoSettingRequestDto repoSettingRequestDto, MultipartFile images);
    // 레포 정보 수정

    // 레포 갱신


}