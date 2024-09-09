package com.server.pnd.readme.service;

import com.server.pnd.diagram.service.QuestionService;
import com.server.pnd.domain.Readme;
import com.server.pnd.domain.Repo;
import com.server.pnd.gpt.dto.ChatCompletionDto;
import com.server.pnd.gpt.dto.ChatRequestMsgDto;
import com.server.pnd.readme.dto.*;
import com.server.pnd.readme.repository.ReadmeRepository;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadmeServiceImpl implements ReadmeService{
    private final ReadmeRepository readmeRepository;
    private final RepoRepository repoRepository;
    private final QuestionService questionService;

    // 리드미 문서 저장
    @Override
    public ResponseEntity<CustomApiResponse<?>> savedReadme(ReadmeSavedRequestDto readmeSavedRequestDto) {
        Optional<Repo> foundRepo = repoRepository.findById(readmeSavedRequestDto.getRepoId());
        // Id에 해당하는 레포가 없는 경우 : 404
        if (foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포 Id에 해당하는 레포가 DB에 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();

        // content가 비어있거나 공백문자만 있는 경우 : 400
        String content = readmeSavedRequestDto.getContent();
        if (content.isBlank()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(400, "내용은 비어있을 수 없습니다.");
            return ResponseEntity.status(400).body(res);
        }

        Optional<Readme> foundReadme = readmeRepository.findByRepo(repo);
        Readme readme;

        // 이미 레포에 리드미가 있는 경우
        if (foundReadme.isPresent()) {
            readme = foundReadme.get();
            readme.setContent(content);
        }else{
            // 레포에 리드미가 없는 경우
            readme = Readme.builder()
                    .repo(repo)
                    .readme_script(content)
                    .build();
        }

        // DB에 저장
        readmeRepository.save(readme);

        // data 가공
        ReadmeSavedResponseDto data = ReadmeSavedResponseDto.builder()
                .readmeId(readme.getId()).build();

        // 리드미 저장 성공 : 201
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "리드미 파일 저장 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }

    // 리드미 상세 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> searchReadme(Long repoId) {
        Optional<Repo> foundRepo = repoRepository.findById(repoId);

        // 해당 Repo ID의 Repo가 DB에 없는 경우 : 404
        if (foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 Id의 레포가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();

        Optional<Readme> foundReadme = readmeRepository.findByRepo(repo);
        // 해당 ID의 리드미가 DB에 없는 경우 : 404
        if (foundReadme.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포의 리드미가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Readme readme = foundReadme.get();

        // data
        ReadmeDetailDto data = ReadmeDetailDto.builder()
                .id(readme.getId())
                .readmeScript(readme.getReadme_script())
                .createdAt(readme.localDateTimeToString())
                .build();

        // 조회 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "리드미 상세 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 리드미 자동 생성
    @Override
    public ResponseEntity<CustomApiResponse<?>> generateReadmeWithGpt(Long repoId) {
        Optional<Repo> foundRepo = repoRepository.findById(repoId);

        // 해당 Repo ID의 Repo가 DB에 없는 경우 : 404
        if(foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 Id의 레포가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();

        Optional<Readme> foundReadme = readmeRepository.findByRepo(repo);
        Readme readme;
        // 리드미 엔티티가 존재하는지 확인
        if(foundReadme.isPresent()) {
            readme = foundReadme.get();
            String existingScript = readme.getReadme_script_gpt();
            // readme_script_gpt가 이미 존재하는 경우, 이를 반환
            if(existingScript != null && !existingScript.isBlank()) {
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, existingScript, "이미 저장된 GPT 스크립트를 반환합니다."));
            }
        } else {
            // Readme 엔티티가 존재하지 않으면 새로 생성
            readme = Readme.builder().repo(repo).build();
        }

        // 리드미 자동 생성 요청 GPT 프롬프트
        String prompt =
                "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. 프로젝트를 전체적으로 설명할 수 있도록 리드미 코드 형태로 생성해줘. 혹시 레포지토리 타이틀이 길어 잘리게 된다면, 폰트 크기는 알아서 조절해줘. 템플릿은 예시 그대로 적용하고 내용만 바꿔주면 돼. 별다른 설명할 필요없이 예시로 제공하는 것처럼 코드블록만 제공해줘\n" +
                        "<예시>\n" +
                        "[질문]\n" +
                        "https://github.com/Hjwoon/Ch-Eating-BE\n" +
                        "[답변]\n" +
                        "```\n" +
                        "<div align=\"center\">\n" +
                        "  <img src=\"https://capsule-render.vercel.app/api?type=venom&color=auto&height=300&section=header&text=Ch-Eating-BE&fontSize=90&animation=fadeIn&desc=Backend%20Service%20for%20Ch-Eating&descAlignY=65&descAlign=62\"&textBackground=true alt=\"header\"/>\n" +
                        "<h3>\uD83D\uDC4B Welcome to Ch-Eating Backend \uD83D\uDC4B</h3>\n" +
                        "\n" +
                        "\n" +
                        "![GitHub stars](https://img.shields.io/github/stars/Hjwoon/Ch-Eating-BE?style=social)\n" +
                        "![GitHub license](https://img.shields.io/github/license/Hjwoon/Ch-Eating-BE)\n" +
                        "\n" +
                        "Efficient backend services for Ch-Eating, handling user authentication, data management, and more.\n" +
                        "<br><br>\n" +
                        "\n" +
                        "<div align=\"center\">\n" +
                        "  <h3>\uD83D\uDCDA Tech Stack \uD83D\uDCDA</h3>\n" +
                        "  <div>\n" +
                        "    <img src=\"https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white\" alt=\"Java\" />\n" +
                        "    <img src=\"https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white\" alt=\"Spring Boot\" />\n" +
                        "    <img src=\"https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white\" alt=\"MySQL\" />\n" +
                        "  </div>\n" +
                        "  <div>\n" +
                        "    <img src=\"https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=Amazon%20AWS&logoColor=white\" alt=\"AWS\" />\n" +
                        "    <img src=\"https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white\" alt=\"Docker\" />\n" +
                        "  </div>\n" +
                        "  <br>\n" +
                        "</div>\n" +
                        "\n" +
                        "<div align=\"center\">\n" +
                        "  <h3>\uD83C\uDF08 Follow Me \uD83C\uDF08</h3>\n" +
                        "<span>\n" +
                        "    <a href=\"https://hyejinworkspace.notion.site/HyeJin-Portfolio-fec8d9843fae4152a7996d8f3301e6e4?pvs=4\">\n" +
                        "    <img src=\"https://img.shields.io/badge/portfolio-faf082?style=for-the-badge&logo=youtubegaming&logoColor=white\"/>\n" +
                        "  </a>\n" +
                        "</span>\n" +
                        "<span>\n" +
                        "    <a href=\"https://hyejinworkspace.notion.site/HyeJin-fb9e294c759548829fef74e3bc8c43b4?pvs=4\">\n" +
                        "    <img src=\"https://img.shields.io/badge/Study-d2e1ff?style=for-the-badge&logo=codeigniter&logoColor=white\"/>\n" +
                        "  </a>\n" +
                        "</span>\n" +
                        "<span>\n" +
                        "  <a href=\"mailto:olivia019182@gmail.com\">\n" +
                        "    <img src=\"https://img.shields.io/badge/Email-black?style=for-the-badge&logo=Gmail&logoColor=white\"/>\n" +
                        "  </a>\n" +
                        "</span>\n" +
                        "<span>\n" +
                        "  <a href=\"https://www.instagram.com/khyaejin/\">\n" +
                        "    <img src=\"https://img.shields.io/badge/Instagram-C13584?style=for-the-badge&logo=Instagram&logoColor=white\"/>\n" +
                        "  </a>\n" +
                        "</span>\n" +
                        "</div><br>\n" +
                        "\n" +
                        "<div align=\"center\">\n" +
                        "<h3>\uD83D\uDC69\u200D\uD83D\uDCBB My GitHub Status \uD83D\uDC69\u200D\uD83D\uDCBB</h3>\n" +
                        "  \n" +
                        "<div align = \"center\">\n" +
                        "  \n" +
                        "<!-- used Languages-->\n" +
                        "<img src=\"https://github-readme-stats.vercel.app/api/top-langs/?username=Hjwoon&layout=compact&hide_border=true&bg_color=30,91eae4,86A8E7&title_color=fff&text_color=fff\" alt=\"Top Langs\" width=40%/>\n" +
                        "\n" +
                        "<!-- trophy-->\n" +
                        "<img src=\"https://github-profile-trophy.vercel.app/?username=Hjwoon&margin-w=15&row=2&column=4&no-frame=true\" alt=\"GitHub Trophy\" widtd=58%/>\n" +
                        "\n" +
                        "<!-- git review -->\n" +
                        "  ![Anurag's GitHub stats](https://github-readme-stats.vercel.app/api?username=Hjwoon&show_icons=true&hide=stars)\n" +
                        "</div>\n" +
                        "\n" +
                        "<div align=\"center\">\n" +
                        "  <br>\n" +
                        "  <a href=\"https://hits.seeyoufarm.com\">\n" +
                        "    <img src=\"https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FHjwoon%2FCh-Eating-BE&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false\" alt=\"Hits\" />\n" +
                        "  </a>\n" +
                        "</div>\n" +
                        "```\n" +
                        "\n" +
                        "https://github.com/Hjwoon/CareerDoctor-Backend";

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
        String result = questionService.callGptApi(chatCompletionDto);
        // 결과를 Readme 엔티티에 저장
        readme.setReadmeScriptGpt(result);
        readmeRepository.save(readme);

        ReadmeAutoCreateResponseDto data = ReadmeAutoCreateResponseDto.builder()
                .readme_script_gpt(result)
                .build();

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "리드미 자동 생성이 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 리드미 수정
    @Override
    public ResponseEntity<CustomApiResponse<?>> editReadme(ReadmeEditRequestDto readmeEditRequestDto) {
        Optional<Readme> foundReadme = readmeRepository.findById(readmeEditRequestDto.getReadmeId());
        // Id에 해당하는 리드미가 없는 경우 : 404
        if (foundReadme.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 리드미 Id에 해당하는 리드미가 DB에 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Readme readme = foundReadme.get();

        String content = readmeEditRequestDto.getContent();
        // content가 비어있거나 공백문자만 있는 경우 : 400
        if (content.isBlank()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(400, "내용은 비어있을 수 없습니다.");
            return ResponseEntity.status(400).body(res);
        }

        // 내용 수정
        readme.setContent(content);

        // DB에 저장
        readmeRepository.save(readme);

        // data 가공
        ReadmeEditResponseDto data = ReadmeEditResponseDto.builder()
                .content(content).build();

        // 리드미 수정 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "리드미 파일 수정 완료되었습니다.");
        return ResponseEntity.status(200).body(res);    }


}