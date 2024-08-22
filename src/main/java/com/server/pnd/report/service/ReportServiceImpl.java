package com.server.pnd.report.service;

import com.server.pnd.domain.Repo;
import com.server.pnd.domain.User;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.report.dto.EventInfoDto;
import com.server.pnd.report.dto.GitHubEvent;
import com.server.pnd.user.repository.UserRepository;
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
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    final private RepoRepository repoRepository;
    final private UserRepository userRepository;
    private final RestTemplate restTemplate;

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
        String accessToken = user.getAccessToken();
        String username = user.getName();
        String url = String.format("https://api.github.com/users/%s/events/public", username);

        // 깃허브 api를 사용해 event 불러오기
        GitHubEvent[] events = getEventsFromGithub(accessToken, username, url);

        // 레고 블럭 생성 (Node.js 스크립트 실행)
        ProcessBuilder processBuilder = new ProcessBuilder("ts-node", "src/main/resources/scripts/3d-contrib/index.ts");

        // 환경 변수 설정
        processBuilder.environment().put("USERNAME", username);
        processBuilder.environment().put("GITHUB_TOKEN", accessToken);

        // 스크립트 실행 및 결과 확인
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("3D 그래프 생성 중 오류 발생");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 레포트 생성
        makeReportImg();

        // 레포트 불러오기
        BufferedImage image = loadGeneratedImage();
        return null;
    }

    // 깃허브 이벤트 불러오기
    public GitHubEvent[] getEventsFromGithub(String accessToken, String username, String url){
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.set("Content-type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 요청 보내기
        ResponseEntity<EventInfoDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, EventInfoDto.class);

        EventInfoDto eventInfoDto = response.getBody();

        return null;
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
