package com.server.pnd.project.controller;

import com.server.pnd.project.dto.ProjectCreatedRequestDto;
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
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody ProjectCreatedRequestDto projectCreatedRequestDto) {
        return projectService.createProject(authorizationHeader, projectCreatedRequestDto);
    }

    // 프로젝트 전체 조회
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchProjectList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return projectService.searchProjectList(authorizationHeader);
    }

    // 프로젝트 상세 조회
    @GetMapping("/{project_id}")
    public ResponseEntity<CustomApiResponse<?>> searchProjectDetail(
            @PathVariable("project_id") Long projectId){
        return projectService.searchProjectDetail(projectId);
    }
}
