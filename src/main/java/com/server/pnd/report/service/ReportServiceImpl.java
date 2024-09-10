package com.server.pnd.report.service;

import com.server.pnd.domain.Repo;
import com.server.pnd.domain.Report;
import com.server.pnd.domain.User;
import com.server.pnd.oauth.service.SocialLoginService;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.report.dto.GitHubEvent;
import com.server.pnd.report.dto.ReportDetailDto;
import com.server.pnd.report.repository.ReportRepository;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.InputStreamReader;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final RepoRepository repoRepository;
    private final ReportRepository reportRepository;
    private final RestTemplate restTemplate;
    private final SocialLoginService socialLoginService;
    private final GitHubGraphQLService gitHubGraphQLService;

    // 레포트 생성
    @Override
    public ResponseEntity<CustomApiResponse<?>> createReport(Long repoId) {
        //404 : 해당 레포가 없는 경우
        Optional<Repo> foundRepo = repoRepository.findById(repoId);
        if (foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포지토리를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();
        User user = repo.getUser();

        // 엑세스 토큰, URL 설정
        // socialLoginService.refreshGitHubAccessToken(user); //토큰 업데이트
        String accessToken = user.getAccessToken();
        String username = user.getName();
        String repositoryName = repo.getRepoName();

        // GitHub GraphQL API 사용하여 데이터 가져오기
        String response = gitHubGraphQLService.fetchUserData(accessToken, username, repositoryName);
        System.err.println("response: " + response);

        // 레고 블럭 생성 (Node.js 스크립트실행)
        ProcessBuilder processBuilder = new ProcessBuilder("ts-node", "src/main/resources/scripts/3d-contrib/src/index.ts");

        // 환경 변수 설정
        processBuilder.environment().put("GITHUB_DATA", response);
        processBuilder.environment().put("USERNAME", username);
//        processBuilder.environment().put("GITHUB_TOKEN", accessToken);

        // 스크립트 실행 및 결과 확인
        try {
            System.out.println("Starting Node.js script...");

            // 프로세스 시작
            Process process = processBuilder.start();

            // 표준 출력을 읽어서 출력
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = stdOutput.readLine()) != null) {
                System.out.println(line);
            }

            // 표준 오류를 읽어서 출력
            while ((line = stdError.readLine()) != null) {
                System.err.println("Node.js Error: " + line);
            }

            // 프로세스 종료 코드 확인
            int exitCode = process.waitFor();
            System.out.println("Node.js script finished with exit code: " + exitCode);

            if (exitCode != 0) {
                throw new RuntimeException("3D 그래프 생성 중 오류 발생, exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 레포트 생성
        // makeReportImg();

        // 레포트 불러오기
        BufferedImage image = loadGeneratedImage();
        return null;
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
                .image(report.getImage())
                .createdAt(report.localDateTimeToString())
                .build();

        // 레포트 상세조회 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "레포트 상세 조회 성공했습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 깃허브 이벤트 불러오기
    public GitHubEvent[] getEventsFromGithub(String accessToken, String username, String url){
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.set("Content-type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 보내기 및 응답 받기
        ResponseEntity<GitHubEvent[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, GitHubEvent[].class);
        return response.getBody();  // GitHubEvent 배열 반환
    }

    // report 생성
    public void makeReportImg() {
        // 이미지 생성
        BufferedImage img = new BufferedImage(1416, 726, BufferedImage.TYPE_INT_RGB);
        // Graphics2D를 얻어와 그림을 그림
        Graphics2D graphics = img.createGraphics();
        try{
            // 파일의 이름 설정
            File file = new File("/Users/gimhyejin/Desktop/imgtest.jpg");
            // write메소드를 이용해 파일을 만듦
            ImageIO.write(img, "jpg", file);
        }
        catch(Exception e){e.printStackTrace();}

    }

    // 이미지 반환
    private BufferedImage loadGeneratedImage() {
        try {
            File file = new File("/Users/gimhyejin/Desktop/imgtest.jpg");
            return ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}