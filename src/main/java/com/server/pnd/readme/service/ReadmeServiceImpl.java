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
        String prompt = "내가 제공하는 링크로 접속하여 깃 레파지토리내의 모든 디렉토리 및 코드를 확인해줘. 프로젝트를 전체적으로 설명할 수 있도록 리드미 코드 형태로 생성해줘. 혹시 레포지토리 타이틀이 길어 잘리게 된다면, 폰트 크기는 알아서 조절해줘. 템플릿은 예시 그대로 적용하고 내용만 바꿔주면 돼. 별다른 설명할 필요 없이 예시로 제공하는 것처럼 **마크다운 문법**을 사용하여 제공해줘. tech stack은 해당 링크의 레포지토리에서 사용한 언어와 개발 환경에 맞게 변경해야 해. 마크다운 문법으로 된 코드도 해당 레포지토리에서 사용된 언어만 존재하도록 바꿔야 해. 코드는 다음 블로그를 참조하여 선택해줘.(https://velog.io/@cha-suyeon/github-%EA%B9%83%ED%97%88%EB%B8%8C-%EB%A6%AC%EB%93%9C%EB%AF%B8%EC%97%90%EC%84%9C-%EB%B1%83%EC%A7%80-%EB%A7%8C%EB%93%A4%EA%B8%B0) 설명 또한 마찬가지야. 프로젝트 구조 또한 제공되는 링크에 맞게 유동적으로 변경해야 해. \n" +
                "작성하는 리드미의 모든 내용은 **직접** 링크를 타고 들어가서 내용 확인하고 레포지토리에 맞게 **마크다운 문법**으로 작성해줘. Project Structure에서 구조는 하위항목이 없는 경우 하나로 묶어서 main/java/com 이런식으로 표기해 줘. " +
                "\n" +
                "\n" +
                "<예시 1>\n" +
                "[질문]\n" +
                "https://github.com/TeamKioki/FE\n" +
                "\n" +
                "[답변]\n" +
                "![Kioki Frontend](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=Kioki%20Frontend&fontSize=30&fontAlign=50)\n" +
                "\n" +
                "## \uD83D\uDC4B Welcome to Kioki Frontend \uD83D\uDC4B\n" +
                " The **Kioki Frontend** is responsible for rendering the user interface, handling interactions, and managing state using Redux. It interfaces with the backend to fetch and display data, ensuring a seamless user experience on the Kioki platform.\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px;\">\n" +
                "    <img src=\"https://img.shields.io/github/stars/benniejung/Kioki-FE?style=social\" alt=\"GitHub stars\">\n" +
                "    <img src=\"https://img.shields.io/github/license/benniejung/Kioki-FE\" alt=\"GitHub license\">\n" +
                "</p>\n" +
                "\n" +
                "\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;\">\n" +
                "    <img src=\"https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=React&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/Redux-764ABC?style=for-the-badge&logo=Redux&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/Styled--Components-DB7093?style=for-the-badge&logo=styled-components&logoColor=white\">\n" +
                "</p>\n" +
                "\n" +
                "\n" +
                "- \uD83D\uDCBB **JavaScript** and **React**: Core technologies used for creating a dynamic and interactive user interface.\n" +
                "- \uD83D\uDEE0\uFE0F **Redux**: State management for the application.\n" +
                "- ⚙\uFE0F **Styled-Components**: For styling React components in a modular and maintainable way.\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- **\uD83D\uDDC2\uFE0F src**: Main source directory containing all application code.\n" +
                "  - **\uD83D\uDCE6 components**: Reusable UI components.\n" +
                "  - **\uD83D\uDCE6 containers**: Components connected to Redux and contain application logic.\n" +
                "  - **\uD83D\uDD27 redux**: Redux setup and state management.\n" +
                "    - **\uD83D\uDD28 actions**: Action creators for Redux.\n" +
                "    - **⚙\uFE0F reducers**: Reducers to handle state changes.\n" +
                "    - **\uD83D\uDEE0\uFE0F store.js**: Configuration and initialization of Redux store.\n" +
                "  - **\uD83D\uDDBC\uFE0F assets**: Static assets like images and stylesheets.\n" +
                "  - **\uD83E\uDDF0 utils**: Utility functions and helpers.\n" +
                "  - **\uD83D\uDE80 App.js**: Main application component.\n" +
                "  - **\uD83D\uDD11 index.js**: Entry point of the application.\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1\uFE0F⃣ **Clone the repository:**\n" +
                "```\n" +
                "git clone https://github.com/benniejung/Kioki-FE.git\n" +
                "```\n" +
                "\n" +
                "2\uFE0F⃣ Navigate to the project directory:\n" +
                "```\n" +
                "cd Kioki-FE\n" +
                "```\n" +
                "\n" +
                "3\uFE0F⃣ Install dependencies:\n" +
                "```\n" +
                "npm install\n" +
                "```\n" +
                "\n" +
                "4\uFE0F⃣ Start the application:\n" +
                "```\n" +
                "npm start\n" +
                "```\n" +
                "\n" +
                "\n" +
                "\uD83D\uDCC4 **License** \uD83D\uDCC4  \n" +
                "This project is licensed under the MIT License. For more details, please refer to the [LICENSE](./LICENSE) file.\n" +
                "\n" +
                "\n" +
                "\uD83C\uDF1F **About this README** \uD83C\uDF1F  \n" +
                "This README provides a clear and concise overview of the project's key information, including the tech stack, project structure, and setup instructions.\n" +
                "\n" +
                "\n" +
                "✨ **Special Thanks** ✨  \n" +
                "A special shout-out to the **P-ND** team, brought to you by **Gamjakkang**! \uD83C\uDF89  \n" +
                "This README was crafted with their amazing assistance. \n" +
                "\n" +
                "Check out more about **P-ND** at the [P-ND GitHub repository](https://github.com/PND-Gamjakkang). \uD83D\uDE80\n" +
                "\n" +
                "<예시 2>\n" +
                "[질문]\n" +
                "https://github.com/Hjwoon/CareerDoctor-Backend\n" +
                "\n" +
                "[답변]\n" +
                "![CareerDoctor Backend](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=CareerDoctor%20Backend&fontSize=30&fontAlign=50)\n" +
                "\n" +
                "## \uD83D\uDC4B Welcome to CareerDoctor Backend \uD83D\uDC4B\n" +
                "The **CareerDoctor Backend** is responsible for managing user data, authentication, and handling API services that support the CareerDoctor platform. It provides a robust backend solution to ensure the platform operates efficiently.\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px;\">\n" +
                "    <img src=\"https://img.shields.io/github/stars/HSU-Likelion-CareerDoctor/CareerDoctor-Backend?style=social\" alt=\"GitHub stars\">\n" +
                "    <img src=\"https://img.shields.io/github/license/HSU-Likelion-CareerDoctor/CareerDoctor-Backend\" alt=\"GitHub license\">\n" +
                "</p>\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;\">\n" +
                "    <img src=\"https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white\">\n" +
                "</p>\n" +
                "\n" +
                "- \uD83D\uDCBB **Java** and **Spring Boot**: Used for building backend services and APIs.\n" +
                "- \uD83D\uDEE0\uFE0F **MySQL**: Database for handling and storing user data.\n" +
                "\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- **\uD83D\uDDC2\uFE0F src**: Main source directory containing application code.\n" +
                "  - **\uD83D\uDCE6 controllers**: Manages HTTP requests.\n" +
                "  - **\uD83D\uDCE6 services**: Contains business logic.\n" +
                "  - **\uD83D\uDCE6 repositories**: Handles database operations.\n" +
                "  - **\uD83D\uDDBC\uFE0F resources**: Configuration and static resources.\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1\uFE0F⃣ **Clone the repository:**\n" +
                "```\n" +
                "git clone https://github.com/HSU-Likelion-CareerDoctor/CareerDoctor-Backend.git\n" +
                "```\n" +
                "\n" +
                "2\uFE0F⃣ Navigate to the project directory:\n" +
                "```\n" +
                "cd CareerDoctor-Backend\n" +
                "```\n" +
                "\n" +
                "3\uFE0F⃣ Install dependencies:\n" +
                "```\n" +
                "./gradlew build\n" +
                "```\n" +
                "\n" +
                "4\uFE0F⃣ Run the application:\n" +
                "```\n" +
                "./gradlew bootRun\n" +
                "```\n" +
                "\n" +
                "\uD83D\uDCC4 License \uD83D\uDCC4\n" +
                "This project is licensed under the MIT License. For more details, see the LICENSE file.\n" +
                "\n" +
                "\uD83C\uDF1F About this README \uD83C\uDF1F\n" +
                "This README provides an overview of the backend's key features, tech stack, and project structure.\n" +
                "\n" +
                "✨ Special Thanks ✨\n" +
                "Special thanks to the P-ND team, powered by Gamjakkang! \uD83D\uDE80\n" +
                "For more info, check out the P-ND GitHub repository.\n" +
                "<예시 3>\n" +
                "[질문]\n" +
                "https://github.com/Hjwoon/Mini_OverWatch\n" +
                "\n" +
                "[답변]\n" +
                "![Mini OverWatch](https://capsule-render.vercel.app/api?type=rect&color=gradient&text=Mini_OverWatch&fontSize=30&fontAlign=50)\n" +
                "\n" +
                "## \uD83D\uDC4B Welcome to Mini_OverWatch \uD83D\uDC4B\n" +
                "The **Mini OverWatch** project is a bullet-style game built using Java. It includes both a game authoring tool for creating custom games and a player mode to enjoy those creations. The tool allows users to design levels, place obstacles, enemies, and set custom backgrounds.\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px;\">\n" +
                "    <img src=\"https://img.shields.io/github/stars/Hjwoon/Mini_OverWatch?style=social\" alt=\"GitHub stars\">\n" +
                "    <img src=\"https://img.shields.io/github/license/Hjwoon/Mini_OverWatch\" alt=\"GitHub license\">\n" +
                "</p>\n" +
                "\n" +
                "## \uD83D\uDCDA Tech Stack \uD83D\uDCDA\n" +
                "\n" +
                "<p align=\"center\" style=\"display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;\">\n" +
                "    <img src=\"https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white\">\n" +
                "    <img src=\"https://img.shields.io/badge/XML-FF6600?style=for-the-badge&logo=xml&logoColor=white\">\n" +
                "</p>\n" +
                "\n" +
                "- \uD83D\uDCBB **Java**: Used for the main game and authoring tool development.\n" +
                "- \uD83D\uDEE0\uFE0F **XML**: For storing and loading game levels and configurations.\n" +
                "\n" +
                "## \uD83D\uDCC2 Project Structure \uD83D\uDCC2\n" +
                "\n" +
                "- **\uD83D\uDDC2\uFE0F Wproject**: Contains the main game source files.\n" +
                "  - **\uD83D\uDCE6 AuthorFrame.java**: Handles the game authoring tool interface.\n" +
                "  - **\uD83D\uDCE6 GameApp.java**: Manages the main gameplay logic.\n" +
                "\n" +
                "## \uD83D\uDE80 Getting Started \uD83D\uDE80\n" +
                "\n" +
                "1\uFE0F⃣ **Clone the repository:**\n" +
                "```\n" +
                "git clone https://github.com/Hjwoon/Mini_OverWatch.git\n" +
                "```\n" +
                "\n" +
                "2\uFE0F⃣ Navigate to the project directory:\n" +
                "```\n" +
                "cd Mini_OverWatch\n" +
                "```\n" +
                "\n" +
                "3\uFE0F⃣ Compile the project:\n" +
                "```\n" +
                "javac -d bin Wproject/*.java\n" +
                "```\n" +
                "\n" +
                "4\uFE0F⃣ Run the application:\n" +
                "```\n" +
                "java -cp bin Wproject.GameApp\n" +
                "```\n" +
                "\n" +
                "\uD83D\uDCC4 License \uD83D\uDCC4\n" +
                "This project is licensed under the Apache-2.0 License. For more details, see the LICENSE file.\n" +
                "\n" +
                "\uD83C\uDF1F About this README \uD83C\uDF1F\n" +
                "This README provides an overview of the game's core features, tech stack, and project structure.\n" +
                "\n" +
                "✨ Special Thanks ✨\n" +
                "Special thanks to the P-ND team, powered by Gamjakkang! \uD83D\uDE80\n" +
                "For more info, check out the P-ND GitHub repository.\n" +
                "\n" +
                "```\n" +
                "This README template has been customized for the **Mini OverWatch** repository, reflecting its key technologies, structure, and features.\n" +
                "```" +
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