package com.server.pnd.readme.service;

import com.server.pnd.readme.dto.ReadmeEditRequestDto;
import com.server.pnd.readme.dto.ReadmeSavedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ReadmeService {

    // 리드미  저장
    ResponseEntity<CustomApiResponse<?>> savedReadme(ReadmeSavedRequestDto markdownSavedRequestDto);

    // 리드미 상세 조회
    ResponseEntity<CustomApiResponse<?>> searchReadme(Long readmeId);

    // 리드미 자동 생성
    ResponseEntity<CustomApiResponse<?>> generateReadmeWithGpt(Long repoId);

    // 리드미 수정
    ResponseEntity<CustomApiResponse<?>> editReadme(ReadmeEditRequestDto readmeEditRequestDto);


}
