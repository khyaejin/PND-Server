package com.server.pnd.report.service;

import com.server.pnd.diagram.service.QuestionService;
import com.server.pnd.domain.Repo;
import com.server.pnd.domain.Report;
import com.server.pnd.domain.User;
import com.server.pnd.gpt.dto.ChatCompletionDto;
import com.server.pnd.gpt.dto.ChatRequestMsgDto;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.report.dto.CreateReportResponseDto;
import com.server.pnd.report.dto.ReportDetailDto;
import com.server.pnd.report.repository.ReportRepository;
import com.server.pnd.s3.config.S3Config;
import com.server.pnd.util.response.CustomApiResponse;
import com.server.pnd.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final RepoRepository repoRepository;
    private final ReportRepository reportRepository;
    private final GitHubGraphQLService gitHubGraphQLService;
    private final S3Service s3Service;
    private final S3Config s3Config;
    private final QuestionService questionService;


    // 레포트 생성
    @Override
    public ResponseEntity<CustomApiResponse<?>> createReport(Long repoId) {
        Process process = null;
        try {
            // 404 : 해당 레포가 없는 경우
            Optional<Repo> foundRepo = repoRepository.findById(repoId);
            if (foundRepo.isEmpty()) {
                CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(res);
            }
            Repo repo = foundRepo.get();
            Optional<User> foundUser = Optional.ofNullable(repo.getUser());

            // 404 : 해당 유저가 없는 경우
            if (foundUser.isEmpty()) {
                CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포의 유저를 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(res);
            }
            User user = foundUser.get();

            // 엑세스 토큰, URL 설정
            // socialLoginService.refreshGitHubAccessToken(user); // 토큰 업데이트
            String accessToken = user.getAccessToken();
            String username = user.getName();
            String repositoryName = repo.getRepoName();
            String organizationName = repo.getOrganizationName();

            // 데이터 세팅 ----------------------------------------------------------------------------------------

            // GitHub GraphQL API 사용하여 데이터 가져오기
            String response = gitHubGraphQLService.fetchUserData(accessToken, username, organizationName, repositoryName);
            System.err.println("response: " + response);

            // GPT API 사용하여 회고 정보 가져오기
            String prompt =
                    "내가 제공하는 링크로 접속하여 깃 레파지토리 내의 모든 디렉토리 및 코드를 확인해줘. 프로젝트를 전체적으로 리뷰 및 회고를 할 수 있도록 회고 가이드를 생성해줘. 혹시 레포지토리 타이틀이 길어 잘리게 된다면, 폰트 크기는 알아서 조절해줘. 템플릿은 예시 그대로 적용하고 내용만 바꿔주면 돼. 별다른 설명할 필요 없이 예시로 제공하는 것처럼 **마크다운 문법**을 사용하여 코드블록만 제공해줘.  \n" +
                            "회고 방법 추천은 KPT(Keep, Problem, Try), CSS(Continue, Stop, Start), Mad-Sad-Glad, 4Ls 중에서 레포지토리에 가장 어울리는 걸로 골라주고 그 이유도 아래 예시와 같이 써줘. 회고 방법 설명에는 해당 회고 방법의 진행 방법을 간단하게 써주면 돼. \n" +
                            "최대한 사용자가 레포지토리별로 맞춤 회고 가이드를 받는다고 느끼도록 섬세하고 차별화된 가이드를 제공해줘. \n" +
                            "\n" +
                            "---\n" +
                            "\n" +
                            "<예시 1>  \n" +
                            "[질문]  \n" +
                            "https://github.com/Likelion-All-Together/all-together-front  \n" +
                            "\n" +
                            "[답변]  \n" +
                            "# \uD83D\uDD04 All Together 프론트엔드 프로젝트 회고가이드 \uD83D\uDD04\n" +
                            "\n" +
                            "## \uD83C\uDFAF 회고 방법 추천: **KPT (Keep, Problem, Try)**  \n" +
                            "All Together 프로젝트는 프론트엔드에서 유지보수성과 확장성에 중점을 두었기 때문에 **KPT** 방식을 추천드립니다. KPT는 현재 잘되고 있는 부분(Keep), 문제점(Problem), 시도해볼 점(Try)을 구분하여, 각 기능에 대한 피드백을 명확히 할 수 있습니다. 이 방식은 프론트엔드의 디테일한 피드백을 제공하는 데 효과적입니다.\n" +
                            "\n" +
                            "## \uD83D\uDCE2 회고 방법 설명  \n" +
                            "KPT는 유지할 점, 문제점, 시도할 점을 나눠서 피드백을 작성합니다. 팀원들과 회의를 통해 개선 방안을 도출하고 구체적인 시도할 항목을 정의합니다.\n" +
                            "\n" +
                            "## \uD83C\uDF08 회고를 위한 기본 요소  \n" +
                            "\uD83D\uDD05 **개방성과 솔직함**  \n" +
                            "\uD83D\uDD05 **비판적 사고**  \n" +
                            "\uD83D\uDD05 **학습 중심**\n" +
                            "\n" +
                            "## \uD83D\uDCA1 프로젝트 개요  \n" +
                            "All Together 프로젝트는 사용자 친화적인 웹 애플리케이션을 목표로 개발되었습니다.\n" +
                            "\n" +
                            "## ✅ AI 한줄 평가  \n" +
                            "### \uD83D\uDC4F 잘된 점  \n" +
                            "- **모듈화된 컴포넌트 구조**: 유지보수와 확장성이 뛰어났습니다.  \n" +
                            "- **협업 도구 사용**: GitHub의 Pull Request와 코드 리뷰로 효율성을 높였습니다.\n" +
                            "\n" +
                            "### \uD83D\uDD27 개선할 점  \n" +
                            "- **로딩 속도 최적화**: 성능 개선이 필요합니다.  \n" +
                            "- **테스트 부족**: 더 많은 테스트 케이스가 필요합니다.\n" +
                            "\n" +
                            "### \uD83D\uDE80 주요 교훈  \n" +
                            "- **모듈화 중요성**: 재사용 가능하고 유지보수가 용이한 코드 구조를 유지하는 것이 핵심임을 배웠습니다.\n" +
                            "\n" +
                            "### \uD83D\uDCA1 회고하면 좋을 것들  \n" +
                            "1. **UI/UX 피드백 반영**  \n" +
                            "2. **기술 부채 관리**  \n" +
                            "3. **CI/CD 파이프라인 구축 경험**\n" +
                            "\n" +
                            "---\n" +
                            "\n" +
                            "<예시 2>  \n" +
                            "[질문]  \n" +
                            "https://github.com/khyaejin/YourSide-Server  \n" +
                            "\n" +
                            "[답변]  \n" +
                            "# \uD83D\uDD04 YourSide-Server 백엔드 프로젝트 회고가이드 \uD83D\uDD04\n" +
                            "\n" +
                            "## \uD83C\uDFAF 회고 방법 추천: **4Ls (Liked, Learned, Lacked, Longed for)**  \n" +
                            "YourSide-Server 프로젝트는 백엔드의 데이터 관리 및 추천 시스템 개발이 중점이었기 때문에 **4Ls** 방식을 추천드립니다. 4Ls는 팀원들이 프로젝트에서 좋았던 점(Liked), 배운 점(Learned), 부족했던 점(Lacked), 더 원했던 점(Longed for)을 공유하며, 개선 사항을 도출하기에 효과적입니다.\n" +
                            "\n" +
                            "## \uD83D\uDCE2 회고 방법 설명  \n" +
                            "4Ls 방식에서는 각 팀원이 위 네 가지 질문에 답변하며, 프로젝트의 성과와 개선점을 도출합니다. 회고를 통해 각자가 배운 점을 나눔으로써 더욱 발전할 수 있습니다.\n" +
                            "\n" +
                            "## \uD83C\uDF08 회고를 위한 기본 요소  \n" +
                            "\uD83D\uDD05 **개방성과 솔직함**  \n" +
                            "\uD83D\uDD05 **비판적 사고**  \n" +
                            "\uD83D\uDD05 **학습 중심**\n" +
                            "\n" +
                            "## \uD83D\uDCA1 프로젝트 개요  \n" +
                            "YourSide-Server는 사용자 데이터 관리 및 추천 시스템을 제공하는 백엔드 프로젝트입니다.\n" +
                            "\n" +
                            "## ✅ AI 한줄 평가  \n" +
                            "### \uD83D\uDC4F 잘된 점  \n" +
                            "- **데이터 관리 효율성**: 데이터베이스 설계가 효율적이었습니다.  \n" +
                            "- **Spring Boot 활용**: 안정적인 배포와 빠른 서비스 제공이 가능했습니다.\n" +
                            "\n" +
                            "### \uD83D\uDD27 개선할 점  \n" +
                            "- **API 성능 최적화**: 데이터 처리 속도를 개선할 필요가 있었습니다.  \n" +
                            "- **테스트 부족**: 더 많은 테스트 커버리지가 필요합니다.\n" +
                            "\n" +
                            "### \uD83D\uDE80 주요 교훈  \n" +
                            "- **데이터 최적화**: 빠른 응답을 위해 API 개선이 필요합니다.\n" +
                            "\n" +
                            "### \uD83D\uDCA1 회고하면 좋을 것들  \n" +
                            "1. **데이터 구조 최적화**  \n" +
                            "2. **API 성능 향상 방법**  \n" +
                            "3. **테스트 자동화 도입**\n" +
                            "\n" +
                            "---\n" +
                            "\n" +
                            "<예시 3>  \n" +
                            "[질문]  \n" +
                            "https://github.com/khyaejin/Scapture-Server  \n" +
                            "\n" +
                            "[답변]  \n" +
                            "# \uD83D\uDD04 Scapture-Server 백엔드 프로젝트 회고가이드 \uD83D\uDD04\n" +
                            "\n" +
                            "## \uD83C\uDFAF 회고 방법 추천: **CSS (Continue, Stop, Start)**  \n" +
                            "Scapture-Server 프로젝트는 복잡한 데이터 처리 로직과 빠른 응답이 중요한 백엔드 시스템이었기 때문에 **CSS** 방식을 추천드립니다. CSS 방식은 현재 유지할 것(Continue), 중단할 것(Stop), 새로 시작할 것(Start)을 명확히 구분하여, 성과를 유지하면서 개선할 사항을 도출하는 데 유리합니다.\n" +
                            "\n" +
                            "## \uD83D\uDCE2 회고 방법 설명  \n" +
                            "CSS 방식은 팀원들이 각 항목에 대해 나눠 토론하며 유지해야 할 점과 중단할 점을 정리하고, 새로 시도할 항목을 계획합니다.\n" +
                            "\n" +
                            "## \uD83C\uDF08 회고를 위한 기본 요소  \n" +
                            "\uD83D\uDD05 **개방성과 솔직함**  \n" +
                            "\uD83D\uDD05 **비판적 사고**  \n" +
                            "\uD83D\uDD05 **학습 중심**\n" +
                            "\n" +
                            "## \uD83D\uDCA1 프로젝트 개요  \n" +
                            "Scapture 서버는 사용자 데이터 처리 및 이미지 분석 기능을 지원하는 백엔드 프로젝트입니다.\n" +
                            "\n" +
                            "## ✅ AI 한줄 평가  \n" +
                            "### \uD83D\uDC4F 잘된 점  \n" +
                            "- **데이터 분석 로직**: 고성능의 데이터 처리 로직이 설계되었습니다.  \n" +
                            "- **RESTful API 설계**: API 설계가 간결하고 효율적이었습니다.\n" +
                            "\n" +
                            "### \uD83D\uDD27 개선할 점  \n" +
                            "- **확장성 부족**: 일부 모듈의 확장성 문제를 겪었습니다.  \n" +
                            "- **로그 관리 부족**: 로그 기록이 미흡했습니다.\n" +
                            "\n" +
                            "### \uD83D\uDE80 주요 교훈  \n" +
                            "- **확장성의 중요성**: 확장 가능성을 고려한 설계의 필요성을 배웠습니다.\n" +
                            "\n" +
                            "### \uD83D\uDCA1 회고하면 좋을 것들  \n" +
                            "1. **확장 가능한 구조 설계**  \n" +
                            "2. **로그 관리 자동화**  \n" +
                            "3. **API 버전 관리**";
            // 시스템 메시지 생성
            List<ChatRequestMsgDto> messages = List.of(
                    ChatRequestMsgDto.builder().role("system").content(prompt).build(),
                    ChatRequestMsgDto.builder().role("user").content(repo.getRepoURL()).build()
            );

            // ChatCompletionDto 객체 생성
            ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                    .model("gpt-4o")
                    .messages(messages)
                    .build();

            // GPT API 호출 및 결과 저장
            String retroResponse = questionService.callGptApi(chatCompletionDto);
            System.out.println("retroResponse: "+ retroResponse);
            // 데이터 세팅 끝 ------------------------------------------------------------------------------------------

            // ProcessBuilder 절대 경로 설정
            String os = System.getProperty("os.name").toLowerCase();

            //ts-node 경로 변경
            String scriptPath;

            if (os.contains("win")) {
                // Windows path
                scriptPath = "./src/main/resources/scripts/3d-contrib/src/index.ts";
            } else if (os.contains("mac")) {
                // macOS path
                scriptPath = "/Users/gimhyejin/Library/CloudStorage/OneDrive-한성대학교/문서/Projects/PND-Server/src/main/resources/scripts/3d-contrib/src/index.ts";
            } else {
                // Deploy path for EC2 (Linux)
                scriptPath = "../../src/main/resources/scripts/3d-contrib/src/index.ts";
            }
            System.out.println("os: " + os);

            // ProcessBuilder 실행 전 작업 디렉토리 로그 추가
            System.out.println("실행_전_작업_디렉토리: " + new File(".").getAbsolutePath());

            ProcessBuilder processBuilder = new ProcessBuilder("ts-node", scriptPath);

            // ProcessBuilder 실행 후 로그 추가
            System.out.println("ProcessBuilder_실행_후_작업_디렉토리: " + processBuilder.directory());

            // 환경 변수 설정
            processBuilder.environment().put("GITHUB_DATA", response);
            processBuilder.environment().put("USERNAME", username);
            processBuilder.environment().put("GITHUB_DATA_RETRO", retroResponse);

            // 경로확인(테스트)
            System.out.println("Current_Working_Directory: " + Paths.get("").toAbsolutePath().toString());

            // 환경변수 확인(테스트)
            System.out.println("GITHUB_DATA: " + processBuilder.environment().get("GITHUB_DATA"));
            System.out.println("USERNAME: " + processBuilder.environment().get("USERNAME"));
            System.out.println("GITHUB_DATA_RETRO: " + processBuilder.environment().get("GITHUB_DATA_RETRO"));

            // 스크립트 실행 및 결과 확인
            System.out.println("Starting Node.js script...");
            process = processBuilder.start();

            // 표준 출력 및 표준 오류 스트림을 별도로 처리
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            List<String> generatedFileNames = new ArrayList<>();
            String line;

            // 표준 출력 읽기
            while ((line = stdOut.readLine()) != null) {
                System.out.println("stdout: " + line); // 출력된 내용 확인
                if (line.endsWith(".svg")) { // SVG 파일 이름을 포함하는 줄을 찾음
                    generatedFileNames.add(line.trim()); // 파일 이름 저장
                }
            }

            // 표준 오류 읽기
            while ((line = stdErr.readLine()) != null) {
                System.err.println("stderr: " + line); // 표준 오류 출력
            }

            // 프로세스 종료 코드 확인
            int exitCode = process.waitFor();
            System.out.println("Node.js script finished with exit code: " + exitCode);

            if (exitCode != 0) {
                // 오류 출력
                throw new RuntimeException("레포트 생성 중 오류 발생, exit code: " + exitCode);
            }

            String[] imageUrl = new String[8]; // 배포 이미지 url

            if (!generatedFileNames.isEmpty()) {
                System.out.println("Generated SVG files: " + String.join(", ", generatedFileNames));

                int i = 0;
                for (String svgFileName : generatedFileNames) {
                    if (i >= imageUrl.length) {
                        break; // 배열 크기를 초과하지 않도록 안전 장치
                    }

                    // 파일 경로 설정
                    String outputPath;
                    if (os.contains("win")) {
                        // Windows path
                        outputPath = "./src/main/resources/profile-3d-contrib/" + svgFileName;
                    } else if (os.contains("mac")) {
                        // macOS path
                        outputPath = "/Users/gimhyejin/Library/CloudStorage/OneDrive-한성대학교/문서/Projects/PND-Server/src/main/resources/profile-3d-contrib/" + svgFileName;
                    } else {
                        // Deploy path for EC2 (Linux)
                        outputPath = "../../src/main/resources/profile-3d-contrib/" + svgFileName;

                    }

                    File file = new File(outputPath);

                    // S3에 파일 업로드 & 파일(사진) 링크 저장
                    imageUrl[i] = s3Service.upload(file, username, svgFileName);

                    // 해당 file 지우기
                    if (file.delete()) {
                        System.out.println("file 삭제 성공 : " + file.getPath());
                    } else {
                        System.out.println("file 삭제 실패 : " + file.getPath());
                    }

                    // 생성된 Report에 대한 정보 출력
                    System.out.println("Report created with image URL: " + imageUrl[i]);

                    i++;
                }

                Optional<Report> foundReport = reportRepository.findByRepo(repo);
                Report report;

                // 이미 존재하는 report가 있는 경우 -> 업데이트
                if (foundReport.isPresent()) {
                    // 기존 report 가져오기
                    report = foundReport.get();
                    // 기존 필드 수정
                    report.setImageGreen(imageUrl[0]);
                    report.setImageSeason(imageUrl[1]);
                    report.setImageNorthSeason(imageUrl[2]);
                    report.setImageSouthSeason(imageUrl[3]);
                    report.setImageNightView(imageUrl[4]);
                    report.setImageNightGreen(imageUrl[5]);
                    report.setImageNightRainbow(imageUrl[6]);
                    report.setImageGitblock(imageUrl[7]);
                    // DB에 업데이트
                    reportRepository.save(report);
                } else {
                    // 존재하지 않는 경우 -> 새로 삽입
                    report = Report.builder()
                            .repo(repo)
                            .imageGreen(imageUrl[0])
                            .imageSeason(imageUrl[1])
                            .imageNorthSeason(imageUrl[2])
                            .imageSouthSeason(imageUrl[3])
                            .imageNightView(imageUrl[4])
                            .imageNightGreen(imageUrl[5])
                            .imageNightRainbow(imageUrl[6])
                            .imageGitblock(imageUrl[7])
                            .build();
                    reportRepository.save(report);
                }

                // 201 : 레포트 생성 성공
                CreateReportResponseDto data = CreateReportResponseDto.builder()
                        .id(report.getId())
                        .repoTitle(repo.getTitle()) // 레포의 제목
                        .imageGreen(imageUrl[0])
                        .imageSeason(imageUrl[1])
                        .imageNorthSeason(imageUrl[2])
                        .imageSouthSeason(imageUrl[3])
                        .imageNightView(imageUrl[4])
                        .imageNightGreen(imageUrl[5])
                        .imageNightRainbow(imageUrl[6])
                        .imageGitblock(imageUrl[7])
                        .createdAt(reportRepository.findByRepo(repo).get().localDateTimeToString()) // 마지막으로 저장된 Report의 시간 가져오기
                        .build();

                CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "레포트 생성 성공했습니다.");
                return ResponseEntity.status(201).body(res);
            } else {
                throw new RuntimeException("SVG 파일 생성 중 오류 발생, 파일 이름을 찾을 수 없음.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(500, "서버 오류로 인해 레포트를 생성할 수 없습니다.");
            return ResponseEntity.status(500).body(res);
        } catch (InterruptedException e) {
            e.printStackTrace();
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(500, "프로세스 실행 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(res);
        } catch (Exception e) {
            e.printStackTrace();

            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(500, "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(res);
        }
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> searchDetail(Long repoId) {
        // 해당 레포가 없는 경우 : 404
        Optional<Repo> foundRepo = repoRepository.findById(repoId);
        if (foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();

        // 해당 레포의 레포트가 없는 경우 : 404
        Optional<Report> foundReport = reportRepository.findByRepo(repo);
        if (foundReport.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포의 레포트가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Report report = foundReport.get();

        // data
        ReportDetailDto data = ReportDetailDto.builder()
                .id(report.getId())
                .repoTitle(repo.getTitle())
                .imageGreen(report.getImageGreen())
                .imageSeason(report.getImageSeason())
                .imageSouthSeason(report.getImageSouthSeason())
                .imageNightView(report.getImageNightView())
                .imageNightGreen(report.getImageNightGreen())
                .imageNightRainbow(report.getImageNightRainbow())
                .imageGitblock(report.getImageGitblock())
                .createdAt(report.localDateTimeToString())
                .build();

        // 레포트 상세조회 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "레포트 상세 조회 성공했습니다.");
        return ResponseEntity.status(200).body(res);
    }

}