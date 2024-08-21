package com.server.pnd.readme.controller;

import com.server.pnd.readme.dto.ReadmeSavedRequestDto;
import com.server.pnd.readme.service.ReadmeService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/readme")
public class ReadmeController {
    private final ReadmeService readmeService;

    // 마크다운 문서 저장
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> savedReadme(
            @RequestBody ReadmeSavedRequestDto readmeSavedRequestDto) {
        return readmeService.savedReadme(readmeSavedRequestDto);
    }

    // 마크다운 문서 전체 조회
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchReadmeList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return readmeService.searchReadmeList(authorizationHeader);
    }

    // 마크다운 문서 세부 조회
    @GetMapping("/{readme_id}")
    public ResponseEntity<CustomApiResponse<?>> searchReadme(@PathVariable("readme_id") Long readmeId) {
        return readmeService.searchReadme(readmeId);
    }
}
