package com.server.pnd.test.service;

import com.server.pnd.diagram.repository.ClassDiagramRepository;
import com.server.pnd.domain.Diagram;
import com.server.pnd.domain.Project;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.test.dto.ClassDiagramCreatedRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final RepoRepository projectRepository;
    private final ClassDiagramRepository classDiagramRepository;

    @Override
    public ResponseEntity<CustomApiResponse<?>> createClassDiagram(ClassDiagramCreatedRequestDto classDiagramCreatedRequestDto) {
        Optional<Project> foundProject = projectRepository.findById(classDiagramCreatedRequestDto.getProjectId());

        // 프로젝트 ID에 해당하는 프로젝트가 없는 경우 : 404
        if (foundProject.isEmpty()) {
            return ResponseEntity.status(404).body(CustomApiResponse.createFailWithoutData(404, "해당 ID를 가진 프로젝트가 존재하지 않습니다."));
        }
        Project project = foundProject.get();

        String flowchart = "       diagram\n" +
                "            GameController --> GameFrame\n" +
                "            GameController --> WordGenerator\n" +
                "            GameController --> Timer\n" +
                "            GameFrame --> Player\n" +
                "            GameFrame --> Word\n" +
                "            GameFrame --> Score\n" +
                "            GameController : +startGame()\n" +
                "            GameController : +endGame()\n" +
                "            GameController : +updateGame()\n" +
                "            class GameController {\n" +
                "            +List<Word> words\n" +
                "            +Player player\n" +
                "            +Score score\n" +
                "            +Timer timer\n" +
                "            +startGame()\n" +
                "            +endGame()\n" +
                "            +updateGame()\n" +
                "            }\n" +
                "            class GameFrame {\n" +
                "            +displayWord()\n" +
                "            +displayScore()\n" +
                "            +displayTime()\n" +
                "            }\n" +
                "            class WordGenerator {\n" +
                "            +generateWord()\n" +
                "            }\n" +
                "            class Timer {\n" +
                "            +int timeLeft\n" +
                "            +start()\n" +
                "            +stop()\n" +
                "            +countdown()\n" +
                "            }\n" +
                "            class Player {\n" +
                "            +String name\n" +
                "            +int score\n" +
                "            +typeWord()\n" +
                "            }\n" +
                "            class Word {\n" +
                "            +String text\n" +
                "            +int position\n" +
                "            +move()\n" +
                "            +checkTyped()\n" +
                "            }\n" +
                "            class Score {\n" +
                "            +int points\n" +
                "            +increment()\n" +
                "            +reset()\n" +
                "            }"; //gpt api를 사용하여 이 부분 가공

        // save
        Diagram diagram = Diagram.builder()
                .project(project)
                .flowchart(flowchart)
                .build();
        classDiagramRepository.save(diagram);
        return ResponseEntity.status(201).body(CustomApiResponse.createSuccess(201, null,"플로우차트 생성 완료되었습니다."));
    }
}
