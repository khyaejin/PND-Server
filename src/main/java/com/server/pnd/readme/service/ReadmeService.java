package com.server.pnd.readme.service;

import com.server.pnd.readme.dto.ReadmeSavedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ReadmeService {

    // 마크다운 문서 저장
    ResponseEntity<CustomApiResponse<?>> savedReadme(ReadmeSavedRequestDto markdownSavedRequestDto);

    // 마크다운 문서 전체 조회
    ResponseEntity<CustomApiResponse<?>> searchReadmeList(String authorizationHeader);

    // 마크다운 문서 세부 조회
    ResponseEntity<CustomApiResponse<?>> searchReadme(Long markdownId);
}
