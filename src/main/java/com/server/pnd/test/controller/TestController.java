package com.server.pnd.test.controller;

import com.server.pnd.project.dto.ProjectCreatedRequestDto;
import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.test.service.TestService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    // 클래스 다이어그램 생성 테스트 API
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> createClassDiagram(
            @RequestBody ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto) {
        return testService.createClassDiagram(classDiagramCreatedRequestDto);
    }
}
