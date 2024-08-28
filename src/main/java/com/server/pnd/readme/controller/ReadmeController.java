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

    // 리드미 문서 저장
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> savedReadme(
            @RequestBody ReadmeSavedRequestDto readmeSavedRequestDto) {
        return readmeService.savedReadme(readmeSavedRequestDto);
    }

    // 리드미 문서 상세 조회
    @GetMapping("/{repo_id}")
    public ResponseEntity<CustomApiResponse<?>> searchReadme(@PathVariable("repo_id") Long repoId) {
        return readmeService.searchReadme(repoId);
    }

    // 리드미 자동생성

    // 리드미 수정
    @PutMapping("/{readme_id}")
    public ResponseEntity<CustomApiResponse<?>> editReadme(@PathVariable("readme_id") Long readme Id String content) {
        return readmeService.editReadme(content)
    }
}
