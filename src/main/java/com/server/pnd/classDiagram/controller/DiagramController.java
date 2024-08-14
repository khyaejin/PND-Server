package com.server.pnd.classDiagram.controller;

import com.server.pnd.classDiagram.dto.DiagramRequestDto;
import com.server.pnd.classDiagram.service.DiagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // RESTful 컨트롤러로 지정
@RequiredArgsConstructor
@RequestMapping("api/pnd/diagram")
public class DiagramController {
    private final DiagramService diagramService;

    // 클래스 다이어그램 답변 채택하기
    @PostMapping("/class")
    public ResponseEntity<?> recieveClassDiagramAnswer(@RequestBody DiagramRequestDto requestDto) {
        return diagramService.recieveClassDiagramAnswer(requestDto);
    }

    // 시퀀스 다이어그램 답변 채택하기
    @PostMapping("/sequence")
    public ResponseEntity<?> recieveSequenceDiagramAnswer(@RequestBody DiagramRequestDto requestDto) {
        return diagramService.recieveSequenceDiagramAnswer(requestDto);
    }
}
