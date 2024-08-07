package com.server.pnd.project.controller;

import com.server.pnd.project.dto.ProjectCreatedDto;
import com.server.pnd.project.service.ProjectService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd/project")
public class ProjectController {
    private final ProjectService projectService;
    // 프로젝트 생성
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> createProject(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody ProjectCreatedDto projectCreatedDto) {
        return projectService.createProject(authorizationHeader, projectCreatedDto);
    }
}
