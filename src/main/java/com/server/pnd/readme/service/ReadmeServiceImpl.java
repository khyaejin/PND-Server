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
                ReadmeAutoCreateResponseDto data = ReadmeAutoCreateResponseDto.builder()
                        .readme_script_gpt(existingScript)
                        .build();
                return ResponseEntity.ok(CustomApiResponse.createSuccess(200, data, "이미 저장된 GPT 스크립트를 반환합니다."));
            }
        } else {
            // Readme 엔티티가 존재하지 않으면 새로 생성
            readme = Readme.builder().repo(repo).build();
        }

        // 리드미 자동 생성 요청 GPT 프롬프트
        String prompt = "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. 프로젝트를 전체적으로 설명할 수 있도록 리드미 코드 형태로 생성해줘. 혹시 레포지토리 타이틀이 길어 잘리게 된다면, 폰트 크기는 알아서 조절해줘. 템플릿은 예시 그대로 적용하고 내용만 바꿔주면 돼. 별다른 설명할 필요 없이 예시로 제공하는 것처럼 **마크다운 문법**을 사용하여 코드블록만 제공해줘. tech stack은 해당 링크의 레포지토리에서 사용한 언어와 개발 환경에 맞게 변경해야 해. 마크다운 문법으로 된 코드도 해당 레포지토리에서 사용된 언어만 존재하도록 바꿔야 해. 코드는 다음 블로그를 참조하여 선택해줘.(https://velog.io/@cha-suyeon/github-%EA%B9%83%ED%97%88%EB%B8%8C-%EB%A6%AC%EB%93%9C%EB%AF%B8%EC%97%90%EC%84%9C-%EB%B1%83%EC%A7%80-%EB%A7%8C%EB%93%A4%EA%B8%B0) 설명 또한 마찬가지야. 프로젝트 구조 또한 제공되는 링크에 맞게 유동적으로 변경해야 해. \n" +
                "작성하는 리드미의 모든 내용은 **직접** 링크를 타고 들어가서 내용 확인하고 레포지토리에 맞게 **마크다운 문법**으로 작성해줘.\n" +
                "\n" +
                "\n" +
                "<예시 1>\n" +
                "[질문]\n" +
                "https://github.com/Hjwoon/Ch-Eating-BE\n" +
                "\n" +
                "[답변]\n" +
                "![Ch-Eating Backend](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=Ch-Eating%20Backend&fontSize=30&fontAlign=50)\n" +
                "\n" +
                "# \uD83D\uDC4B Welcome to Ch-Eating Backend \uD83D\uDC4B\n" +
                "\n" +
                "![GitHub stars](https://img.shields.io/github/stars/Hjwoon/Ch-Eating-BE?style=social)\n" +
                "![GitHub license](https://img.shields.io/github/license/Hjwoon/Ch-Eating-BE)\n" +
                "\n" +
                "The Ch-Eating Backend is responsible for handling user authentication, data management, and API services that power the Ch-Eating platform.\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)\n" +
                "![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)\n" +
                "![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)\n" +
                "![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=Amazon%20AWS&logoColor=white)\n" +
                "\n" +
                "- **Java** and **Spring Boot**: For building the backend services and RESTful APIs.\n" +
                "- **MySQL**: Database management to store and retrieve user data.\n" +
                "- **AWS**: Cloud platform used for deployment and hosting.\n" +
                "\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- src/main: Contains the main application code.\n" +
                "  - controller: API endpoints for various backend services.\n" +
                "  - service: Business logic for the application.\n" +
                "  - repository: Database interaction layer.\n" +
                "- src/test: Test cases for ensuring code quality.\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1. Clone the repository:\n" +
                "\n" +
                "   \n" +
                "bash\n" +
                "   git clone https://github.com/Hjwoon/Ch-Eating-BE.git\n" +
                "\n" +
                "2. Install dependencies and set up the database (MySQL).\n" +
                "\n" +
                "3. Run the application:\n" +
                "\n" +
                "   ./gradlew bootRun\n" +
                "\n" +
                "<!-- \"https://www.notion.so\" 부분을 원하는 노션 페이지 URL로 변경하세요. -->\n" +
                "[![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)](https://www.notion.so) \n" +
                "\n" +
                "   \uD83D\uDCC4 License \uD83D\uDCC4\n" +
                "This project is licensed under the MIT License. See the LICENSE file for more details.\n" +
                "\n" +
                "\n" +
                "@\n" +
                "This README provides a concise summary of the project and key information regarding the tech stack, structure, and setup instructions.\n" +
                "\n" +
                "<예시 2>\n" +
                "[질문]\n" +
                "https://github.com/Hjwoon/CareerDoctor-Backend\n" +
                "\n" +
                "[답변]\n" +
                "![CareerDoctor Backend](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=CareerDoctor%20Backend&fontSize=30&fontAlign=50)\n" +
                "\n" +
                "# \uD83D\uDC4B Welcome to CareerDoctor Backend \uD83D\uDC4B\n" +
                "\n" +
                "![GitHub stars](https://img.shields.io/github/stars/Hjwoon/CareerDoctor-Backend?style=social)\n" +
                "![GitHub license](https://img.shields.io/github/license/Hjwoon/CareerDoctor-Backend)\n" +
                "\n" +
                "The CareerDoctor Backend provides powerful API services, authentication management, and data processing features to support the CareerDoctor platform.\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)\n" +
                "![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white)\n" +
                "![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)\n" +
                "![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=Amazon%20AWS&logoColor=white)\n" +
                "\n" +
                "- **Java** and **Spring Boot**: Core framework for building RESTful backend services.\n" +
                "- **MySQL**: Relational database for handling data storage.\n" +
                "- **AWS**: Used for deploying the application to the cloud.\n" +
                "\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- src/main: Main application codebase.\n" +
                "  - controller: Defines API endpoints.\n" +
                "  - service: Contains business logic and service layer.\n" +
                "  - repository: Manages database interactions.\n" +
                "- src/test: Unit and integration tests for the application.\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1. Clone the repository:\n" +
                "\n" +
                "   \n" +
                "bash\n" +
                "   git clone https://github.com/Hjwoon/CareerDoctor-Backend.git\n" +
                "\n" +
                "2. Install dependencies and set up the database (MySQL).\n" +
                "\n" +
                "3. Run the application:\n" +
                "\n" +
                "   ./gradlew bootRun \n" +
                "\n" +
                "<!-- \"https://www.notion.so\" 부분을 원하는 노션 페이지 URL로 변경하세요. -->\n" +
                "[![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)](https://www.notion.so) \n" +
                "\n" +
                "\uD83D\uDCC4 License \uD83D\uDCC4\n" +
                "This project is licensed under the MIT License. See the LICENSE file for more details.\n" +
                "\n" +
                "@\n" +
                "This README provides a concise summary of the project and key information regarding the tech stack, structure, and setup instructions.\n" +
                "\n" +
                "<예시 3>\n" +
                "[질문]\n" +
                "https://github.com/Hjwoon/Mini_OverWatch\n" +
                "\n" +
                "[답변]\n" +
                "![Mini OverWatch](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=Mini%20OverWatch%20Backend&fontSize=30&fontAlign=50)\n" +


                "\n" +
                "# \uD83D\uDC4B Welcome to Mini OverWatch \uD83D\uDC4B \n" +
                "\n" +
                "![GitHub stars](https://img.shields.io/github/stars/Hjwoon/Mini_OverWatch?style=social)\n" +
                "![GitHub license](https://img.shields.io/github/license/Hjwoon/Mini_OverWatch)\n" +
                "\n" +
                "The Mini OverWatch Backend handles various aspects of the OverWatch-like platform, including real-time data management, user interactions, and API services.\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)\n" +
                "![XML](https://img.shields.io/badge/XML-FF6600?style=for-the-badge&logo=xml&logoColor=white)\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "The tech stack for the Mini OverWatch project from the repository includes:\n" +
                "\n" +
                "**Java**: Main programming language used.\n" +
                "**XML**: Utilized for configuring game elements.\n" +
                "The project focuses on creating a bullet-game-style application where players can customize and play games through a game editor and player mode.\n" +
                " \n" +
                "\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- **src/main/java**: Contains the main logic for game configuration and player interactions.\n" +
                "  - **game**: Game mechanics such as player, enemy, and object behaviors.\n" +
                "  - **editor**: Code responsible for the Game Editor functionality.\n" +
                "  - **player**: Implements the Player mode to play configured games.\n" +
                "- **src/main/resources**: XML files for game data configuration (e.g., player stats, enemy types).\n" +
                "- **src/test**: Test cases for validating game logic and editor functionality.\n" +
                "\n" +
                "\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1. Clone the repository:\n" +
                "\n" +
                "   \n" +
                "bash\n" +
                "   git clone https://github.com/Hjwoon/Mini_OverWatch.git\n" +
                "\n" +
                "2. Install dependencies and set up the database (MySQL).\n" +
                "\n" +
                "3. Run the application:\n" +
                "\n" +
                "   ./gradlew bootRun \n" +
                "\n" +
                "<!-- \"https://www.notion.so\" 부분을 원하는 노션 페이지 URL로 변경하세요. -->\n" +
                "[![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)](https://www.notion.so) \n" +
                "\n" +
                "\uD83D\uDCC4 License \uD83D\uDCC4\n" +
                "This project is licensed under the MIT License. See the LICENSE file for more details.\n" +
                "\n" +
                "@\n" +
                "This README provides a concise summary of the project and key information regarding the tech stack, structure, and setup instructions.\n" +
                "\n" +
                "[제공하는 링크]\n" + repoId;


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