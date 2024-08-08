package com.server.pnd.test.service;

import com.server.pnd.domain.Project;
import com.server.pnd.domain.User;
import com.server.pnd.project.repository.ProjectRepository;
import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final ProjectRepository projectRepository;

    @Override
    public ResponseEntity<CustomApiResponse<?>> createClassDiagram(ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto) {
        Optional<Project> foundProject = projectRepository.findById(classDiagramCreatedRequestDto.getProjectId());

        // 프로젝트 ID에 해당하는 프로젝트가 없는 경우 : 404
        if (foundProject.isEmpty()) {
            return ResponseEntity.status(404).body(CustomApiResponse.createFailWithoutData(404, "해당 ID를 가진 프로젝트가 존재하지 않습니다."));
        }
        Project project = foundProject.get();

        return null;
    }
}
