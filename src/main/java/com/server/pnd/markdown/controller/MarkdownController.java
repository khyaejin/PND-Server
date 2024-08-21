/*
package com.server.pnd.markdown.controller;

import com.server.pnd.markdown.dto.MarkdownSavedRequestDto;
import com.server.pnd.markdown.service.MarkdownService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/markdown")
public class MarkdownController {
    private final MarkdownService markdownService;

    // 마크다운 문서 저장
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> savedMarkdown(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody MarkdownSavedRequestDto markdownSavedRequestDto) {
        return markdownService.savedMarkdown(authorizationHeader, markdownSavedRequestDto);
    }

    // 마크다운 문서 전체 조회
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchMarkdownList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return markdownService.searchMarkdownList(authorizationHeader);
    }

    // 마크다운 문서 세부 조회
    @GetMapping("/{markdown_id}")
    public ResponseEntity<CustomApiResponse<?>> searchMarkdown(@PathVariable("markdown_id") Long markdownId) {
        return markdownService.searchMarkdown(markdownId);
    }
}
*/
