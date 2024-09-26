package com.server.pnd.diagram.service;

import com.server.pnd.diagram.dto.DiagramRequestDto;
import com.server.pnd.diagram.dto.DiagramUpdateRequestDto;
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

    //
    // 1. 생성

    // GPT 클래스 다이어그램
    // 레포지토리 링크와 함께 질문하여 클래스 다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveClassDiagramAnswer(DiagramRequestDto requestDto) {
        return processDiagramAnswer(requestDto, "classDiagram", Diagram::getClassScriptGpt, Diagram::updateClassScriptGpt);
    }

    // GPT 시퀀스 다이어그램
    // 레포지토리 링크와 함께 질문하여 시퀀스 다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveSequenceDiagramAnswer(DiagramRequestDto requestDto) {
        return processDiagramAnswer(requestDto, "sequenceDiagram", Diagram::getSequenceScriptGpt, Diagram::updateSequenceScriptGpt);
    }

    // GPT ER 다이어그램
    // 레포지토리 링크와 함께 질문하여 ER 다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveERDiagramAnswer(DiagramRequestDto requestDto) {
        return processDiagramAnswer(requestDto, "erDiagram", Diagram::getErdScriptGpt, Diagram::updateErScriptGpt);
    }

    /**
     * GPT 다이어그램 답변을 처리하는 공통 로직을 포함한 메서드.
     * 다이어그램의 유형과 필드 접근 로직을 파라미터로 받아 처리합니다.
     *
     * @param requestDto     다이어그램 요청 DTO
     * @param diagramType    다이어그램 유형 ("classDiagram", "sequenceDiagram", "erDiagram")
     * @param fieldGetter    다이어그램 엔티티에서 GPT 스크립트를 가져오는 함수형 인터페이스
     * @param fieldUpdater   다이어그램 엔티티의 GPT 스크립트를 업데이트하는 함수형 인터페이스
     * @return ResponseEntity  응답 엔티티
     */
    private ResponseEntity<?> processDiagramAnswer(DiagramRequestDto requestDto, String diagramType,
                                                   DiagramFieldGetter fieldGetter, DiagramFieldUpdater fieldUpdater) {
        Long repoId = requestDto.getRepoId();

        // repositoryId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);
        if (optionalRepository.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        Repo repo = optionalRepository.get();
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        Diagram diagram;

        // Diagram 엔티티가 존재하는지 확인
        if (foundDiagram.isPresent()) {
            diagram = foundDiagram.get();
            String existingScript = fieldGetter.getField(diagram);
            // GPT 스크립트가 이미 존재하는 경우, 이를 반환
            if (existingScript != null && !existingScript.isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, existingScript, "이미 저장된 GPT 스크립트를 반환합니다."));
            }
        } else {
            // Diagram 엔티티가 존재하지 않으면 새로 생성
            diagram = Diagram.builder().repo(repo).build();
        }

        // GPT API 호출 및 결과 저장
        String result = callGptAndSaveDiagram(diagramType, repo.getRepoURL(), diagram, fieldUpdater);
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "Open AI API와 성공적으로 통신을 하였습니다."));
    }

    /**
     * GPT API를 호출하고 다이어그램 엔티티에 결과를 저장하는 메서드.
     *
     * @param diagramType    다이어그램 유형 ("classDiagram", "sequenceDiagram", "erDiagram")
     * @param repoUrl        레포지토리 URL
     * @param diagram        다이어그램 엔티티
     * @param fieldUpdater   다이어그램 엔티티의 필드를 업데이트하는 함수형 인터페이스
     * @return String        GPT API 호출 결과
     */
    private String callGptAndSaveDiagram(String diagramType, String repoUrl, Diagram diagram, DiagramFieldUpdater fieldUpdater) {
        // 시스템 메시지 생성
        String systemMessage = generateSystemMessage(diagramType);
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
        // 결과를 Diagram 엔티티에 저장
        fieldUpdater.updateField(diagram, result);
        diagramRepository.save(diagram);

        return result;
    }

    /**
     * 다이어그램 유형에 따라 시스템 메시지를 생성하는 메서드.
     *
     * @param diagramType    다이어그램 유형 ("classDiagram", "sequenceDiagram" 또는 "erDiagram")
     * @return String        생성된 시스템 메시지
     */
    private String generateSystemMessage(String diagramType) {
        String example, diagramExample;
        switch (diagramType) {
            case "sequenceDiagram":
                diagramExample = "sequenceDiagram\n" +
                        "    participant A\n" +
                        "    participant B\n" +
                        "\n" +
                        "    A->>B: Call method1()\n" +
                        "    B-->>A: Return result\n";
                example = "sequenceDiagram\n" +
                        "    participant User as User\n" +
                        "    participant API as API Controller\n" +
                        "    participant Service as Service Layer\n" +
                        "    participant Repo as Repo\n" +
                        "    participant DB as Database\n\n" +
                        "    User->>API: Request API endpoint\n" +
                        "    API->>Service: Call Service method\n" +
                        "    Service->>Repo: Query data\n" +
                        "    Repo->>DB: Execute database query\n" +
                        "    DB-->>Repo: Return query result\n" +
                        "    Repo-->>Service: Return data to Service\n" +
                        "    Service-->>API: Return processed data\n" +
                        "    API-->>User: Send response back to User\n";
                break;
            case "erDiagram":
                diagramExample = "erDiagram\n" +
                        "    A {\n" +
                        "        int variableA\n" +
                        "        string method1()\n" +
                        "    }\n" +
                        "\n" +
                        "    B {\n" +
                        "        string variableB\n" +
                        "        int variableC\n" +
                        "        string method1()\n" +
                        "        string method2()\n" +
                        "    }\n" +
                        "\n" +
                        "    A ||--o{ B : has\n";
                example = "erDiagram\n" +
                        "    USERS {\n" +
                        "        INT id PK\n" +
                        "        VARCHAR username\n" +
                        "        VARCHAR email\n" +
                        "        VARCHAR password\n" +
                        "        DATETIME created_at\n" +
                        "        DATETIME updated_at\n" +
                        "    }\n\n" +
                        "    POSTS {\n" +
                        "        INT id PK\n" +
                        "        VARCHAR title\n" +
                        "        TEXT content\n" +
                        "        INT user_id FK\n" +
                        "        DATETIME created_at\n" +
                        "        DATETIME updated_at\n" +
                        "    }\n\n" +
                        "    USERS ||--o{ POSTS: \"has\"\n";
                break;
            case "classDiagram":
            default:
                diagramExample = "classDiagram\n" +
                        "    class A {\n" +
                        "        +int variableA\n" +
                        "        +method1()\n" +
                        "    }\n" +
                        "\n" +
                        "    class B {\n" +
                        "        +string variableB\n" +
                        "        +int variableC\n" +
                        "        +method1()\n" +
                        "        +method2()\n" +
                        "    }\n" +
                        "\n" +
                        "    A --> B\n";
                example = "classDiagram\n" +
                        "    GameController -> GameFrame\n" +
                        "    GameController -> WordGenerator\n" +
                        "    GameController -> Timer\n" +
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
                        "}\n";
                break;
        }
        System.out.println("example:"+example);

        // 한국어 프롬프트
        return "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. " +
                "코드 구조를 " + diagramType + " 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. " +
                "또한 머메이드 문법 10.9.1에서 작동하도록" + diagramExample + "이와같이 머메이드 문법 10.9.1에서 작동하도록 생성해줘." +
                "특히 클래스 다이어그램 생성에서 클래스 안에는 변수나 메서드만 있어야 해. List<>처럼 다른 자료형은 들어갈 수 없어." +
                "맨 처음에 mermaid 같은 **별다른 설명 없이** 예시로 제공하는 것처럼 다이어그램의 코드블록만 제공해줘. \n<예시>\n[질문]\n" +
                "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n[답변]\n" +
                "```\n" + example + "```\n";

//        // 영어 프롬프트
//        return "Please access the provided link and check all the directories and code within the Git repository. " +
//                "Generate a flowchart text that can draw the code structure in the " + diagramType + " format. Also, create it in a way that works with Mermaid syntax version 10.9.1, just like " + diagramExample +
//                ". Provide the diagram code block only, without any additional explanation, as shown in the example." +
//                "Especially when generating a class diagram, only variables or methods should be inside the class. No other data types are allowed." +
//                "\n<Example>\n[Question]\n" +
//                "https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\n[Answer]\n" + "\n" + example + "\n";
    }

    //
    // 2. 조회

    /**
     * 유저가 수정하고 저장한 클래스 다이어그램 스크립트를 조회하는 메서드.
     *
     * @param repoId     다이어그램 요청 DTO
     * @return String    DB에 저장된 클래스 다이어그램 스크립트 호출 결과
     */
    public ResponseEntity<?> getClassDiagramScript(Long repoId) {

        // repoId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);
        if (optionalRepository.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();
            String foundClassScript = diagram.getClassScript(); // Class 스크립트를 가져옴

            // Class 스크립트가 null이 아니고 비어있지 않은 경우
            if (foundClassScript != null && !foundClassScript.isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, foundClassScript, "Class 다이어그램 스크립트가 성공적으로 조회되었습니다."));
            }
        }
        return ResponseEntity.ok(CustomApiResponse.createSuccess(404, null, "생성되어 있는 Class 다이어그램이 존재하지 않습니다."));

    }

    /**
     * 유저가 수정하고 저장한 시퀀스 다이어그램 스크립트를 조회하는 메서드.
     *
     * @param repoId     다이어그램 요청 DTO
     * @return String    DB에 저장된 시퀀스 다이어그램 스크립트 호출 결과
     */
    public ResponseEntity<?> getSequenceDiagramScript(Long repoId) {

        // repoId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);
        if (optionalRepository.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();
            String foundSequenceScript = diagram.getSequenceScript(); // Sequence 스크립트를 가져옴

            // Sequence 스크립트가 null이 아니고 비어있지 않은 경우
            if (foundSequenceScript != null && !foundSequenceScript.isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, foundSequenceScript, "Sequence 다이어그램 스크립트가 성공적으로 조회되었습니다."));
            }
        }
        return ResponseEntity.ok(CustomApiResponse.createSuccess(404, null, "생성되어 있는 Sequence 다이어그램이 존재하지 않습니다."));

    }

    /**
     * 유저가 수정하고 저장한 ER 다이어그램 스크립트를 조회하는 메서드.
     *
     * @param repoId     다이어그램 요청 DTO
     * @return String    DB에 저장된 ER 다이어그램 스크립트 호출 결과
     */
    public ResponseEntity<?> getErDiagramScript(Long repoId) {

        // repoId를 사용하여 Repo 객체를 조회 (DB에서 가져오기)
        Optional<Repo> optionalRepository = repoRepository.findById(repoId);
        if (optionalRepository.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(repoId);
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();
            String foundErScript = diagram.getErdScript(); // ERD 스크립트를 가져옴

            // ER 스크립트가 null이 아니고 비어있지 않은 경우
            if (foundErScript != null && !foundErScript.isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, foundErScript, "ER 다이어그램 스크립트가 성공적으로 조회되었습니다."));
            }
        }
        return ResponseEntity.ok(CustomApiResponse.createSuccess(404, null, "생성되어 있는 ER 다이어그램이 존재하지 않습니다."));
    }

    // 3. 수정

    /**
     * 유저가 수정한 클래스 다이어그램 스크립트를 DB에 업데이트하는 메서드.
     *
     * @param requestDto 다이어그램 요청 DTO
     * @return ResponseEntity
     */
    public ResponseEntity<?> updateClassDiagramScript(DiagramUpdateRequestDto requestDto) {

        // 1. 요청 데이터 검증
        if (requestDto == null || requestDto.getRepoId() == null || requestDto.getScript() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createSuccess(400, null, "요청 데이터가 올바르지 않습니다."));
        }

        // 2. 기존 다이어그램 찾기
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(requestDto.getRepoId());
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();

            // 3. requestDto의 script를 받아 저장하기
            diagram.updateClassScript(requestDto.getScript());
            diagramRepository.save(diagram);

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, requestDto.getScript(), "클래스 다이어그램 스크립트가 성공적으로 업데이트되었습니다."));
        }

        // 4. 다이어그램을 찾을 수 없는 경우
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.createSuccess(404, null, "생성되어 있는 클래스 다이어그램이 존재하지 않습니다."));
    }

    /**
     * 유저가 수정한 시퀀스 다이어그램 스크립트를 DB에 업데이트하는 메서드.
     *
     * @param requestDto 다이어그램 요청 DTO
     * @return ResponseEntity
     */
    public ResponseEntity<?> updateSequenceDiagramScript(DiagramUpdateRequestDto requestDto) {

        // 1. 요청 데이터 검증
        if (requestDto == null || requestDto.getRepoId() == null || requestDto.getScript() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createSuccess(400, null, "요청 데이터가 올바르지 않습니다."));
        }

        // 2. 기존 다이어그램 찾기
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(requestDto.getRepoId());
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();

            // 3. requestDto의 script를 받아 저장하기
            diagram.updateSequenceScript(requestDto.getScript());
            diagramRepository.save(diagram);

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, requestDto.getScript(), "시퀀스 다이어그램 스크립트가 성공적으로 업데이트되었습니다."));
        }

        // 4. 다이어그램을 찾을 수 없는 경우
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.createSuccess(404, null, "생성되어 있는 시퀀스 다이어그램이 존재하지 않습니다."));

    }

    /**
     * 유저가 수정한 ER 다이어그램 스크립트를 DB에 업데이트하는 메서드.
     *
     * @param requestDto 다이어그램 요청 DTO
     * @return ResponseEntity
     */
    public ResponseEntity<?> updateErDiagramScript(DiagramUpdateRequestDto requestDto) {

        // 1. 요청 데이터 검증
        if (requestDto == null || requestDto.getRepoId() == null || requestDto.getScript() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createSuccess(400, null, "요청 데이터가 올바르지 않습니다."));
        }

        // 2. 기존 다이어그램 찾기
        Optional<Diagram> foundDiagram = diagramRepository.findByRepoId(requestDto.getRepoId());
        if (foundDiagram.isPresent()) {
            Diagram diagram = foundDiagram.get();

            // 3. requestDto의 script를 받아 저장하기
            diagram.updateErScript(requestDto.getScript());
            diagramRepository.save(diagram);

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, requestDto.getScript(), "ER 다이어그램 스크립트가 성공적으로 업데이트되었습니다."));
        }

        // 4. 다이어그램을 찾을 수 없는 경우
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.createSuccess(404, null, "생성되어 있는 ER 다이어그램이 존재하지 않습니다."));

    }

    //
    // 그 외 getter, updater 함수들

    // 다이어그램 필드를 가져오는 함수형 인터페이스
    @FunctionalInterface
    private interface DiagramFieldGetter {
        String getField(Diagram diagram);
    }

    // 다이어그램 필드를 업데이트하는 함수형 인터페이스
    @FunctionalInterface
    private interface DiagramFieldUpdater {
        void updateField(Diagram diagram, String value);
    }
}