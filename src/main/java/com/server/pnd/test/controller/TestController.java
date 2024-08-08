package com.server.pnd.test.controller;

import com.server.pnd.project.dto.ProjectCreatedRequestDto;
import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.test.service.TestService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/test")
public class TestController {
    private final TestService testService;

    // 클래스 다이어그램 생성 테스트 API
    @PostMapping("/diagram")
    public ResponseEntity<CustomApiResponse<?>> createClassDiagram(
            @RequestBody ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto) {
        return testService.createClassDiagram(classDiagramCreatedRequestDto);
    }
}
