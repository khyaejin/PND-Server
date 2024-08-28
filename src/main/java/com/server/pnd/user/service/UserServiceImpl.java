package com.server.pnd.user.service;

import com.server.pnd.diagram.repository.DiagramRepository;
import com.server.pnd.domain.*;
import com.server.pnd.readme.repository.ReadmeRepository;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.report.repository.ReportRepository;
import com.server.pnd.user.dto.SearchProfileResponseDto;
import com.server.pnd.user.dto.SearchRepositoryResponseDto;
import com.server.pnd.user.repository.UserRepository;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RepoRepository repoRepository;
    private final ReadmeRepository readmeRepository;
    private final DiagramRepository diagramRepository;
    private final ReportRepository reportRepository;
    private final JwtUtil jwtUtil;

    // 프로필 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> getProfile(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // 리드미
        int totalReadmes = readmeRepository.countByRepo_User_Id(user.getId()); //userId에 해당하는 리드미 테이블의 개수

        // 다이어그램
        int totalClassDiagram = diagramRepository.countByRepo_User_IdAndClassScriptIsNotNull(user.getId()); //userId에 해당하는 다이어그램 테이블들 중 class_script 필드가 채워져 있는 테이블의 개수
        int totalSequenceDiagram = diagramRepository.countByRepo_User_IdAndSequenceScriptIsNotNull(user.getId()); //userId에 해당하는 다이어그램 테이블들 중 class_script 필드가 채워져 있는 테이블의 개수
        int totalErDiagram = diagramRepository.countByRepo_User_IdAndErdScriptIsNotNull(user.getId()); //userId에 해당하는 다이어그램 테이블들 중 class_script 필드가 채워져 있는 테이블의 개수
        // 생성한 다이어그램 총 개수
        int totalDiagrams = totalClassDiagram + totalSequenceDiagram + totalErDiagram;

        // 리포트
        int totalReports = reportRepository.countByRepo_User_Id(user.getId());
        // 총 문서
        int totalDocs = totalReadmes + totalDiagrams + totalReports; // 생성한 문서 총 개수

        // 프로필 조회 성공 (200)
        SearchProfileResponseDto data = SearchProfileResponseDto.builder()
                .name(user.getName())
                .image(user.getImage())
                .email(user.getEmail())
                .totalDocs(totalDocs)
                .totalReadmes(totalReadmes)
                .totalDiagrams(totalDiagrams)
                .totalReports(totalReports)
                .build();

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "사용자 정보 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);

    }

    // 회원 탈퇴
    @Override
    public ResponseEntity<CustomApiResponse<?>> deleteUser(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // 해당 User를 FK로 가지는 모든 Repo 제거
        List<Repo> repos = repoRepository.findByUserId(user.getId());
        for(Repo repo : repos){
            // 해당 Repo를 FK로 가지는 모든 테이블 제거
            Optional<Readme> foundReadme = readmeRepository.findByRepo(repo);
            foundReadme.ifPresent(readmeRepository::delete);
            Optional<Diagram> foundDiagram = diagramRepository.findByRepo(repo);
            foundDiagram.ifPresent(diagramRepository::delete);
            Optional<Report> foundReport = reportRepository.findByRepo(repo);
            foundReport.ifPresent(reportRepository::delete);

            // 해당 Repo들 제거
            repoRepository.delete(repo);
        }
        // User 제거
        userRepository.delete(user);

        // 삭제
        userRepository.delete(user);

        // 회원 탈퇴 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, null, "회원 탈퇴 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getAllRepository(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        List<Repo> repositories= repoRepository.findByUserId(user.getId());

        // 조회 성공 - 해당 회원의 깃허브 레포지토리가 존재하지 않는 경우 : 200
        if (repositories.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, null, "사용자의 레포지토리가 존재하지 않습니다.");
            return ResponseEntity.status(200).body(res);
        }

        // 조회 성공 - 해당 회원의 깃허브 레포지토리가 존재하는 경우 : 200
        List<SearchRepositoryResponseDto> responseDtos = new ArrayList<>();

        for (Repo repo : repositories) {
            SearchRepositoryResponseDto responseDto = SearchRepositoryResponseDto.builder()
                    .id(repo.getId())
                    .name(repo.getRepoName())
                    .description(repo.getRepoDescription())
                    .stars(repo.getRepoStars())
                    .forksCount(repo.getRepoForksCount())
                    .openIssues(repo.getRepoOpenIssues())
                    .watchers(repo.getRepoWatcher())
                    .language(repo.getRepoLanguage())
                    .createdAt(repo.getFormattedCreatedAt()).build();
            responseDtos.add(responseDto);
        }
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, responseDtos, "레포지토리 전체 조회 성공했습니다.");
        return ResponseEntity.status(200).body(res);
    }
}
