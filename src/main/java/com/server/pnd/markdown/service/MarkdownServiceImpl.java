package com.server.pnd.markdown.service;

import com.server.pnd.markdown.dto.MarkdownSavedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkdownServiceImpl implements MarkdownService{

    // 마크다운 문서 저장
    @Override
    public ResponseEntity<CustomApiResponse<?>> savedMarkdown(String authorizationHeader, MarkdownSavedRequestDto markdownSavedRequestDto) {
        return null;
    }
}
