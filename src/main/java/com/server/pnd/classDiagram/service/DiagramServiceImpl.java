package com.server.pnd.diagram.service;

import com.server.pnd.diagram.dto.DiagramRequestDto;
import com.server.pnd.diagram.dto.DiagramResponseDto;
import com.server.pnd.diagram.repository.DiagramRepository;
import com.server.pnd.domain.ClassDiagram;
import com.server.pnd.domain.Project;
import com.server.pnd.domain.Repository;
import com.server.pnd.domain.User;
import com.server.pnd.project.repository.ProjectRepository;
import com.server.pnd.repository.repository.RepositoryRepository;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagramServiceImpl implements DiagramService {

    // 날짜 포맷터 설정(형식: YYYY-MM-DD)
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DiagramRepository diagramRepository;
    private final RepositoryRepository repositoryRepository;
    private final ChatGPTService chatGPTService;
    private final JwtUtil jwtUtil;
    private final ProjectRepository projectRepository;

    @Override @Transactional
    public CustomApiResponse<DiagramResponseDto> createDiagram(HttpServletRequest request, DiagramRequestDto dto, String authorizationHeader) {
        try {
            Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

            // 토큰에 해당하는 유저가 없는 경우 : 404
            if (foundUser.isEmpty()) {
                return CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            }

            // 프로젝트 조회
            Optional<Project> projectOpt = projectRepository.findById(dto.getProjectId());
            if (projectOpt.isEmpty()) {
                return CustomApiResponse.createFailWithoutData(404, "프로젝트를 찾을 수 없습니다.");
            }
            Project project = projectOpt.get();
            Repository repository = project.getRepository();

            // 프로젝트의 레포지토리 URL 가져오기
            String repoUrl = repository.getHtmlUrl();
            if (repoUrl == null || repoUrl.isEmpty()) {
                return CustomApiResponse.createFailWithoutData(400, "프로젝트에 유효한 레포지토리 URL이 없습니다.");
            }

            // ChatGPT API 호출을 위한 프롬프트 설정
            String prompt = String.format(
                    "Analyze the code structure of the GitHub repository at the following URL: %s. " +
                            "Generate a class diagram in Mermaid format to represent the code structure.",
                    repoUrl
            );

            // ChatGPT API 호출 및 응답 받기
            String flowChartText = chatGPTService.getChatGPTResponse(prompt);

            // 다이어그램 엔티티 생성 및 데이터 설정
            ClassDiagram diagram = ClassDiagram.builder()
                    .repository(repository)
                    .flowchart(flowChartText)
                    .build();

            // 다이어그램 저장
            diagramRepository.save(diagram);

            // 응답 DTO 생성
            DiagramResponseDto responseDto = DiagramResponseDto.builder()
                    .diagramId(diagram.getId())
                    .flowchart(flowChartText)
                    .build();

            // 성공 응답 반환
            return CustomApiResponse.createSuccess(201, responseDto, "플로우차트가 성공적으로 생성되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return CustomApiResponse.createFailWithoutData(500, "서버 오류가 발생했습니다.");
        }

        return null;
    }
}
