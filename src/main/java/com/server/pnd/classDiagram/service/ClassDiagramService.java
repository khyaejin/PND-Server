package com.server.pnd.classDiagram.service;

import com.server.pnd.classDiagram.dto.DiagramRequestDto;
import com.server.pnd.classDiagram.gpt.dto.ChatCompletionDto;
import com.server.pnd.classDiagram.gpt.dto.ChatRequestMsgDto;
import com.server.pnd.domain.Repository;
import com.server.pnd.repository.repository.RepositoryRepository;
import com.server.pnd.util.response.CustomApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassDiagramService {
    private final RepositoryRepository repositoryRepository;
    private final QuestionService questionService;

    // 레포지토리 링크와 함께 질문하여
    // 클래스다이어그램 제작을 위한 플로우차트 답변 받기
    public ResponseEntity<?> recieveAnswer(DiagramRequestDto requestDto) {
        Long repoId = requestDto.getRepositoryId(); // 프론트엔드에서 받은 repositoryId
        // repositoryId를 사용하여 Repository 객체를 조회 (DB에서 가져오기)
        Optional<Repository> optionalRepository = repositoryRepository.findById(repoId);

        // 레포지토리가 존재하지 않는 경우
        if (!optionalRepository.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 레포지토리를 찾을 수 없습니다.");
        }

        // 레포지토리가 존재하는 경우
        Repository repository = optionalRepository.get();
        String repoUrl = repository.getHtmlUrl(); // 레포지토리 링크 가져오기


        // 답변 프롬프트 작성
        List<ChatRequestMsgDto> messages = new ArrayList<>();
        String role = // 시스템 메시지 : GPT 모델에 대해 플로우차트로의 변환을 요청하는 프롬프트
                "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. 코드 구조를 클래스 다이어 그램 형식으로 그릴 수 있도록 플로우 차트 텍스트 형태로 생성해줘. 별다른 설명할 필요없이 예시로 제공하는 것 처럼 다이어그램 코드블록만 제공해줘\n"
                + "<예시>\n" +
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
                "class GameController ‹\n" +
                "+List<Word> words\n" +
                "+Player player\n" +
                "+Score score +Timer timer +startGame ()\n" +
                "+endGame ( )\n" +
                "+updateGame ( )\n" +
                "}\n" +
                "class GameFrame {\n" +
                "+displayWord ()\n" +
                "+displayScore()\n" +
                "+displayTime()\n" +
                "}\n" +
                "class WordGenerator {\n" +
                "+generateWord ()\n" +
                "}\n" +
                "```\n" +
                "\n";

        messages.add(new ChatRequestMsgDto("system", role));
        messages.add(new ChatRequestMsgDto("user", repoUrl)); // 사용자 메시지 : 실제 분석을 원하는 답변 내용

        // ChatCompletionDto 객체를 생성하고, 이 객체 모델 이름과 메시지 리스트 설정
        ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                .model("gpt-4o-mini")
                .messages(messages)
                .build();

        String result = questionService.callGptApi(chatCompletionDto);

        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, result, "ChatGPT로부터 성공적으로 답변을 받았습니다."));
    }
}
