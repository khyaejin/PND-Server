package com.server.pnd.repo.service;

import com.server.pnd.diagram.repository.DiagramRepository;
import com.server.pnd.domain.*;
import com.server.pnd.repo.dto.RepoCreatedRequestDto;
import com.server.pnd.repo.dto.RepoCreatedResponseDto;
import com.server.pnd.repo.dto.RepoSearchDetailResponseDto;
import com.server.pnd.repo.dto.RepoSearchListResponseDto;
import com.server.pnd.repo.repository.RepoRepository;
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
public class RepoServiceImpl implements RepoService {
    private final JwtUtil jwtUtil;
    private final RepoRepository repoRepository;
    private final DiagramRepository classDiagramRepository;

    // 레포 생성
    @Override
    public ResponseEntity<CustomApiResponse<?>> createRepo(String authorizationHeader, RepoCreatedRequestDto projectCreatedRequestDto) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);
        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        Optional<Repo> foundRepository = repoRepository.findById(projectCreatedRequestDto.getRepoId());
        // 해당 Id에 해당하는 레포가 없는 경우 : 404
        if (foundRepository.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 ID를 가진 레포지토리가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepository.get();

        // 레포지토리 생성
        repo = Repo.builder()
                .period(projectCreatedRequestDto.getPeriod())
                .image(projectCreatedRequestDto.getImage())
                .title(projectCreatedRequestDto.getTitle())
                .build();
        repoRepository.save(repo);

        // data
        RepoCreatedResponseDto data = RepoCreatedResponseDto.builder()
                .repoId(repo.getId())
                .build();
        // 프로젝트 생성 성공 : 201
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "프로젝트 생성 완료했습니다.");
        return ResponseEntity.status(201).body(res);
    }

    // 생성한 레포지토리 전체 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> searchRepoList(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);
        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // data
        List<Repo> repos = repoRepository.findByUserId(user.getId());
        List<RepoSearchListResponseDto> data = List.of();
        for (Repo repo : repos) {
            RepoSearchListResponseDto projectSearchListResponseDto = RepoSearchListResponseDto.builder()
                    .image(repo.getImage())
                    .title(repo.getTitle())
                    .build();
            data.add(projectSearchListResponseDto);
        }

        // 성공 - 조회할 프로젝트가 있는 경우 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data,"프로젝트 전체 조회가 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 프로젝트 상세 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> searchRepoDetail(Long repoId) {
        Optional<Repo> foundRepo = repoRepository.findById(repoId);

        // 프로젝트 ID에 해당하는 프로젝트가 없는 경우 : 404
        if (foundRepo.isEmpty()) {
            return ResponseEntity.status(404).body(CustomApiResponse.createFailWithoutData(404, "해당 ID를 가진 프로젝트가 존재하지 않습니다."));
        }
        Repo repo = foundRepo.get();

        Optional<Diagram> foundClassDiagram = classDiagramRepository.findByRepoId(repo.getId());
        // 클래스다이어그램에 플로우차트 존재하지 않음 : 404
        if (foundClassDiagram.isEmpty()) {
            return ResponseEntity.status(404).body(CustomApiResponse.createFailWithoutData(404, "해당 클래스다이어그램에 flowChart가 존재하지 않습니다. (클래스다이어그램 생성시 flowchart 들어가지 않음)"));
        }
        Diagram diagram = foundClassDiagram.get();

        // data
        RepoSearchDetailResponseDto data = RepoSearchDetailResponseDto.builder()
                .title(repo.getTitle())
                .period(repo.getPeriod())
                .createdAt(repo.getCreatedAt())
                .image(repo.getImage())
                .classDiagram(diagram.getClassScript())
                .build();

        // 프로젝트 조회 성공 (200)
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data,"프로젝트 상세조회에 성공했습니다.");
        return ResponseEntity.status(200).body(res);    }
}
