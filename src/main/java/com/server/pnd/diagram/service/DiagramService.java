package com.server.pnd.diagram.service;

import com.server.pnd.diagram.dto.DiagramRequestDto;
import com.server.pnd.diagram.repository.DiagramRepository;
import com.server.pnd.domain.Diagram;
import com.server.pnd.domain.Repo;
import com.server.pnd.gpt.dto.ChatCompletionDto;
import com.server.pnd.gpt.dto.ChatRequestMsgDto;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.util.response.CustomApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DiagramService {
    private final RepoRepository repoRepository;
    private final DiagramRepository diagramRepository;
    private final QuestionService questionService;

    // GPT 클래스 다이어그램
    // 레포지토리 링크와 함께 질문하여 클래스 다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveClassDiagramAnswer(DiagramRequestDto requestDto) {
        Long repoId = requestDto.getRepositoryId(); // 프론트엔드에서 받은 repositoryId

        // repositoryId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);

        // 레포지토리가 존재하지 않는 경우
        if (!optionalRepository.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        // 레포지토리가 존재하는 경우
        Repo repo = optionalRepository.get();

        // Diagram 엔티티가 존재하는지 확인
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        Diagram diagram;

        if (foundDiagram.isPresent()) {
            diagram = foundDiagram.get();
            // classScriptGpt가 null이 아니고, 빈 문자열이 아닌 경우 해당 값을 반환
            if (diagram.getClassScriptGpt() != null && !diagram.getClassScriptGpt().isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, diagram.getClassScriptGpt(), "이미 저장된 GPT 스크립트를 반환합니다."));
            } else {
                // classScriptGpt가 null이거나 비어있다면, GPT API를 호출하여 새로 생성
                String repoUrl = repo.getRepoURL(); // 레포지토리 링크 가져오기

                // 메시지 생성
                String systemMessage =
                        "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. " +
                                "코드 구조를 클래스 다이어그램 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. " +
                                "별다른 설명할 필요없이 예시로 제공하는 것처럼 다이어그램 코드블록만 제공해줘\n" +
                                "<예시>\n" +
                                "[질문]\n" +
                                "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n" +
                                "[답변]\n" +
                                "```\n" +
                                "classDiagram\n" +
                                "    GameController -> GameFrame\n" +
                                "    GameController -> WordGenerator\n" +
                                "GameController -> Timer\n" +
                                "GameFrame -> Player\n" +
                                "GameFrame -> Word\n" +
                                "GameFrame -> Score\n" +
                                "GameController : +startGame()\n" +
                                "GameController : +endGame()\n" +
                                "GameController : +updateGame ()\n" +
                                "class GameController {\n" +
                                "+List<Word> words\n" +
                                "+Player player\n" +
                                "+Score score\n" +
                                "+Timer timer\n" +
                                "+startGame ()\n" +
                                "+endGame ()\n" +
                                "+updateGame ()\n" +
                                "}\n" +
                                "class GameFrame {\n" +
                                "+displayWord ()\n" +
                                "+displayScore()\n" +
                                "+displayTime()\n" +
                                "}\n" +
                                "class WordGenerator {\n" +
                                "+generateWord ()\n" +
                                "}\n" +
                                "```\n";

                List<ChatRequestMsgDto> messages = List.of(
                        ChatRequestMsgDto.builder().role("system").content(systemMessage).build(),
                        ChatRequestMsgDto.builder().role("user").content(repoUrl).build()
                );

                // ChatCompletionDto 객체 생성
                ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                        .model("gpt-4o")
                        .messages(messages)
                        .build();

                // GPT API 호출
                String result = questionService.callGptApi(chatCompletionDto);

                // 결과를 Diagram 엔티티의 classScriptGpt 필드에 저장
                diagram.updateClassScriptGpt(result);
                diagramRepository.save(diagram);  // 엔티티를 저장하여 DB에 반영

                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "Open AI API와 성공적으로 통신을 하였습니다."));
            }
        } else {
            // Diagram 엔티티가 존재하지 않으면 새로 생성
            diagram = Diagram.builder()
                    .repo(repo)
                    .build();

            // 새로운 엔티티에 대해 동일한 GPT API 호출 및 저장 로직을 적용
            String repoUrl = repo.getRepoURL(); // 레포지토리 링크 가져오기

            // 메시지 생성
            String systemMessage =
                    "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. " +
                            "코드 구조를 클래스 다이어그램 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. " +
                            "별다른 설명할 필요없이 예시로 제공하는 것처럼 다이어그램 코드블록만 제공해줘\n" +
                            "<예시>\n" +
                            "[질문]\n" +
                            "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n" +
                            "[답변]\n" +
                            "```\n" +
                            "classDiagram\n" +
                            "    GameController -> GameFrame\n" +
                            "    GameController -> WordGenerator\n" +
                            "GameController -> Timer\n" +
                            "GameFrame -> Player\n" +
                            "GameFrame -> Word\n" +
                            "GameFrame -> Score\n" +
                            "GameController : +startGame()\n" +
                            "GameController : +endGame()\n" +
                            "GameController : +updateGame ()\n" +
                            "class GameController {\n" +
                            "+List<Word> words\n" +
                            "+Player player\n" +
                            "+Score score\n" +
                            "+Timer timer\n" +
                            "+startGame ()\n" +
                            "+endGame ()\n" +
                            "+updateGame ()\n" +
                            "}\n" +
                            "class GameFrame {\n" +
                            "+displayWord ()\n" +
                            "+displayScore()\n" +
                            "+displayTime()\n" +
                            "}\n" +
                            "class WordGenerator {\n" +
                            "+generateWord ()\n" +
                            "}\n" +
                            "```\n";

            List<ChatRequestMsgDto> messages = List.of(
                    ChatRequestMsgDto.builder().role("system").content(systemMessage).build(),
                    ChatRequestMsgDto.builder().role("user").content(repoUrl).build()
            );

            // ChatCompletionDto 객체 생성
            ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                    .model("gpt-4o")
                    .messages(messages)
                    .build();

            // GPT API 호출
            String result = questionService.callGptApi(chatCompletionDto);

            // 결과를 Diagram 엔티티의 classScriptGpt 필드에 저장
            diagram.updateClassScriptGpt(result);
            diagramRepository.save(diagram);  // 엔티티를 저장하여 DB에 반영

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "Open AI API와 성공적으로 통신을 하였습니다."));
        }
    }

    // GPT 시퀀스 다이어그램
    // 레포지토리 링크와 함께 질문하여 시퀀스 다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveSequenceDiagramAnswer(DiagramRequestDto requestDto) {
        Long repoId = requestDto.getRepositoryId(); // 프론트엔드에서 받은 repositoryId

        // repositoryId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);

        // 레포지토리가 존재하지 않는 경우
        if (!optionalRepository.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        // 레포지토리가 존재하는 경우
        Repo repo = optionalRepository.get();

        // Diagram 엔티티가 존재하는지 확인
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        Diagram diagram;

        if (foundDiagram.isPresent()) {
            diagram = foundDiagram.get();
            // sequenceScriptGpt가 null이 아니고, 빈 문자열이 아닌 경우 해당 값을 반환
            if (diagram.getSequenceScriptGpt() != null && !diagram.getSequenceScriptGpt().isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, diagram.getSequenceScriptGpt(), "이미 저장된 GPT 스크립트를 반환합니다."));
            } else {
                // sequenceScriptGpt가 null이거나 비어있다면, GPT API를 호출하여 새로 생성
                String repoUrl = repo.getRepoURL(); // 레포지토리 링크 가져오기

                // 메시지 생성
                String systemMessage =
                        "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. " +
                                "코드 구조를 시퀀스 다이어그램 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. " +
                                "별다른 설명할 필요없이 예시로 제공하는 것처럼 다이어그램 코드블록만 제공해줘\n" +
                                "<예시>\n" +
                                "[질문]\n" +
                                "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n" +
                                "[답변]\n" +
                                "```\n" +
                                "sequenceDiagram\n" +
                                "    participant User as User\n" +
                                "    participant API as API Controller\n" +
                                "    participant Service as Service Layer\n" +
                                "    participant Repo as Repo\n" +
                                "    participant DB as Database\n" +
                                "\n" +
                                "    User->>API: Request API endpoint\n" +
                                "    API->>Service: Call Service method\n" +
                                "    Service->>Repo: Query data\n" +
                                "    Repo->>DB: Execute database query\n" +
                                "    DB-->>Repo: Return query result\n" +
                                "    Repo-->>Service: Return data to Service\n" +
                                "    Service-->>API: Return processed data\n" +
                                "    API-->>User: Send response back to User\n" +
                                "```\n";

                List<ChatRequestMsgDto> messages = List.of(
                        ChatRequestMsgDto.builder().role("system").content(systemMessage).build(),
                        ChatRequestMsgDto.builder().role("user").content(repoUrl).build()
                );

                // ChatCompletionDto 객체 생성
                ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                        .model("gpt-4o")
                        .messages(messages)
                        .build();

                // GPT API 호출
                String result = questionService.callGptApi(chatCompletionDto);

                // 결과를 Diagram 엔티티의 sequenceScriptGpt 필드에 저장
                diagram.updateSequenceScriptGpt(result);
                diagramRepository.save(diagram);  // 엔티티를 저장하여 DB에 반영

                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "Open AI API와 성공적으로 통신을 하였습니다."));
            }
        } else {
            // Diagram 엔티티가 존재하지 않으면 새로 생성
            diagram = Diagram.builder()
                    .repo(repo)
                    .build();

            // 새로운 엔티티에 대해 동일한 GPT API 호출 및 저장 로직을 적용
            String repoUrl = repo.getRepoURL(); // 레포지토리 링크 가져오기

            // 메시지 생성
            String systemMessage =
                    "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. " +
                            "코드 구조를 시퀀스 다이어그램 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. " +
                            "별다른 설명할 필요없이 예시로 제공하는 것처럼 다이어그램 코드블록만 제공해줘\n" +
                            "<예시>\n" +
                            "[질문]\n" +
                            "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n" +
                            "[답변]\n" +
                            "```\n" +
                            "sequenceDiagram\n" +
                            "    participant User as User\n" +
                            "    participant API as API Controller\n" +
                            "    participant Service as Service Layer\n" +
                            "    participant Repo as Repo\n" +
                            "    participant DB as Database\n" +
                            "\n" +
                            "    User->>API: Request API endpoint\n" +
                            "    API->>Service: Call Service method\n" +
                            "    Service->>Repo: Query data\n" +
                            "    Repo->>DB: Execute database query\n" +
                            "    DB-->>Repo: Return query result\n" +
                            "    Repo-->>Service: Return data to Service\n" +
                            "    Service-->>API: Return processed data\n" +
                            "    API-->>User: Send response back to User\n" +
                            "```\n";

            List<ChatRequestMsgDto> messages = List.of(
                    ChatRequestMsgDto.builder().role("system").content(systemMessage).build(),
                    ChatRequestMsgDto.builder().role("user").content(repoUrl).build()
            );

            // ChatCompletionDto 객체 생성
            ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                    .model("gpt-4o")
                    .messages(messages)
                    .build();

            // GPT API 호출
            String result = questionService.callGptApi(chatCompletionDto);

            // 결과를 Diagram 엔티티의 sequenceScriptGpt 필드에 저장
            diagram.updateSequenceScriptGpt(result);
            diagramRepository.save(diagram);  // 엔티티를 저장하여 DB에 반영

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "Open AI API와 성공적으로 통신을 하였습니다."));
        }
    }
}