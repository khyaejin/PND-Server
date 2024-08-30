package com.server.pnd.diagram.controller;

import com.server.pnd.diagram.dto.DiagramRequestDto;
import com.server.pnd.diagram.dto.DiagramResponseDto;
import com.server.pnd.diagram.dto.DiagramUpdateRequestDto;
import com.server.pnd.diagram.service.DiagramService;
import com.server.pnd.domain.Diagram;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // RESTful 컨트롤러로 지정
@RequiredArgsConstructor
@RequestMapping("api/pnd/diagram")
public class DiagramController {
    private final DiagramService diagramService;

    /*
     * 다이어그램 GPT 생성
     */

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


    /*
     * 유저 생성 다이어그램 조회
     */

    // 클래스 다이어그램 조회
    @GetMapping("/class")
    public ResponseEntity<?> getClassDiagramScript(@RequestParam Long repoId) {
        return diagramService.getClassDiagramScript(repoId);
    }

    // 시퀀스 다이어그램 조회
    @GetMapping("/sequence")
    public ResponseEntity<?> getSequenceDiagramScript(@RequestParam Long repoId) {
        return diagramService.getSequenceDiagramScript(repoId);
    }

    // er 다이어그램 조회
    @GetMapping("/er")
    public ResponseEntity<?> getErDiagramScript(@RequestParam Long repoId) {
        return diagramService.getErDiagramScript(repoId);
    }

    /*
     * 유저 생성 다이어그램 수정
     */

    // 클래스 다이어그램 수정
    @PatchMapping("/class")
    public ResponseEntity<?> updateClassDiagramScript(@RequestBody DiagramUpdateRequestDto requestDto) {
        return diagramService.updateClassDiagramScript(requestDto);
    }

    // 시퀀스 다이어그램 수정
    @PatchMapping("/sequence")
    public ResponseEntity<?> updateSequenceDiagramScript(@RequestBody DiagramUpdateRequestDto requestDto) {
        return diagramService.updateSequenceDiagramScript(requestDto);
    }

    // ER 다이어그램 수정
    @PatchMapping("/er")
    public ResponseEntity<?> updateErDiagramScript(@RequestBody DiagramUpdateRequestDto requestDto) {
        return diagramService.updateErDiagramScript(requestDto);
    }

}
