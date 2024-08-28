package com.server.pnd.diagram.controller;

import com.server.pnd.diagram.dto.DiagramRequestDto;
import com.server.pnd.diagram.service.DiagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // RESTful 컨트롤러로 지정
@RequiredArgsConstructor
@RequestMapping("api/pnd/diagram")
public class DiagramController {
    private final DiagramService diagramService;

    // 클래스 다이어그램 GPT 답변 채택하기
    @PatchMapping("/class-gpt")
    public ResponseEntity<?> recieveClassDiagramAnswer(@RequestBody DiagramRequestDto requestDto) {
        return diagramService.recieveClassDiagramAnswer(requestDto);
    }

    // 시퀀스 다이어그램 GPT 답변 채택하기
    @PatchMapping("/sequence-gpt")
    public ResponseEntity<?> recieveSequenceDiagramAnswer(@RequestBody DiagramRequestDto requestDto) {
        return diagramService.recieveSequenceDiagramAnswer(requestDto);
    }

    // ER 다이어그램 GPT 답변 채택하기
    @PatchMapping("/er-gpt")
    public ResponseEntity<?> recieveERDiagramAnswer(@RequestBody DiagramRequestDto requestDto) {
        return diagramService.recieveERDiagramAnswer(requestDto);
    }
}
