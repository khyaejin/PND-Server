package com.server.pnd.project.service;

import com.server.pnd.project.dto.ProjectCreatedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ProjectService {
    // 프로젝트 생성
    ResponseEntity<CustomApiResponse<?>> createProject(String authorizationHeader, ProjectCreatedRequestDto projectCreatedRequestDto);

    // 프로젝트 전체 조회
    ResponseEntity<CustomApiResponse<?>> searchProjectList(String authorizationHeader);

    // 프로젝트 상세 조회
    ResponseEntity<CustomApiResponse<?>> searchProjectDetail(Long projectId);
}