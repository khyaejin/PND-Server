package com.server.pnd.markdown.service;

import com.server.pnd.markdown.dto.MarkdownSavedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface MarkdownService {

    // 마크다운 문서 저장
    ResponseEntity<CustomApiResponse<?>> savedMarkdown(String authorizationHeader, MarkdownSavedRequestDto markdownSavedRequestDto);

    // 마크다운 문서 전체 조회
    ResponseEntity<CustomApiResponse<?>> searchMarkdownList(String authorizationHeader);
}
