package com.server.pnd.classDiagram.service;

import com.server.pnd.classDiagram.dto.DiagramRequestDto;
import com.server.pnd.classDiagram.dto.DiagramResponseDto;
import com.server.pnd.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

public interface DiagramService {
    // 플로우차트(이 패키지 내에서는 쉽게 다이어그램 생성을 위한 데이터 -> Diagram으로 통일) 생성
    CustomApiResponse<DiagramResponseDto> createDiagram(HttpServletRequest request, DiagramResponseDto dto, String authorizationHeader);

    @Transactional
    CustomApiResponse<DiagramResponseDto> createDiagram(HttpServletRequest request, DiagramRequestDto dto, String authorizationHeader);
}
