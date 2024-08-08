package com.server.pnd.test.service;

import com.server.pnd.classDiagram.repository.ClassDiagramRepository;
import com.server.pnd.domain.ClassDiagram;
import com.server.pnd.domain.Project;
import com.server.pnd.project.repository.ProjectRepository;
import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final ProjectRepository projectRepository;
    private final ClassDiagramRepository classDiagramRepository;

    @Override
    public ResponseEntity<CustomApiResponse<?>> createClassDiagram(ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto) {
        Optional<Project> foundProject = projectRepository.findById(classDiagramCreatedRequestDto.getProjectId());

        // 프로젝트 ID에 해당하는 프로젝트가 없는 경우 : 404
        if (foundProject.isEmpty()) {
            return ResponseEntity.status(404).body(CustomApiResponse.createFailWithoutData(404, "해당 ID를 가진 프로젝트가 존재하지 않습니다."));
        }
        Project project = foundProject.get();

        String flowchart = "classDiagram\n" +
                "    direction TB\n" +
                "    \n" +
                "    class ScaptureServer {\n" +
                "        -Logger logger\n" +
                "        +main(args: String[]): void\n" +
                "    }\n" +
                "\n" +
                "    class UserController {\n" +
                "        -UserService userService\n" +
                "        +getUser(id: Long): User\n" +
                "        +createUser(user: User): User\n" +
                "    }\n" +
                "\n" +
                "    class UserService {\n" +
                "        -UserRepository userRepository\n" +
                "        +findUserById(id: Long): User\n" +
                "        +saveUser(user: User): User\n" +
                "    }\n" +
                "\n" +
                "    class UserRepository {\n" +
                "        +findById(id: Long): User\n" +
                "        +save(user: User): User\n" +
                "    }\n" +
                "\n" +
                "    class User {\n" +
                "        -Long id\n" +
                "        -String name\n" +
                "        -String email\n" +
                "        +getId(): Long\n" +
                "        +getName(): String\n" +
                "        +getEmail(): String\n" +
                "        +setId(id: Long): void\n" +
                "        +setName(name: String): void\n" +
                "        +setEmail(email: String): void\n" +
                "    }\n" +
                "\n" +
                "    ScaptureServer --> UserController\n" +
                "    UserController --> UserService\n" +
                "    UserService --> UserRepository\n" +
                "    UserRepository --> User\n"; //gpt api를 사용하여 이 부분 가공

        // save
        ClassDiagram classDiagram = ClassDiagram.builder()
                .project(project)
                .flowchart(flowchart)
                .build();
        classDiagramRepository.save(classDiagram);
        return ResponseEntity.status(201).body(CustomApiResponse.createSuccess(201, null,"플로우차트 생성 완료되었습니다."));
    }
}
