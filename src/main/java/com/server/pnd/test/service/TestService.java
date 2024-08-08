package com.server.pnd.test.service;

import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface TestService {
    // 테스트 API : 통신을 위한 클래스다이어그램 생성 API
    ResponseEntity<CustomApiResponse<?>> createClassDiagram(ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto);
}
