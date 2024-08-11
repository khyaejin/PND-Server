package com.server.pnd.classDiagram.controller;

import com.server.pnd.classDiagram.dto.DiagramRequestDto;
import com.server.pnd.classDiagram.service.ClassDiagramService;
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
    private final ClassDiagramService classDiagramService;

    // 답변 채택하기
    @PostMapping
    public ResponseEntity<?> receiveAnswer(@RequestBody DiagramRequestDto requestDto) {
        return classDiagramService.recieveAnswer(requestDto);
    }
}
