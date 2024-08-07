package com.server.pnd.project.service;

import com.server.pnd.project.dto.ProjectCreatedDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ProjectService {
    ResponseEntity<CustomApiResponse<?>> createProject(String authorizationHeader, ProjectCreatedDto projectCreatedDto);
}
