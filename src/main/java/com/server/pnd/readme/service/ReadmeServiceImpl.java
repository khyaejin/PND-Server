package com.server.pnd.readme.service;

import com.server.pnd.domain.Readme;
import com.server.pnd.domain.Repo;
import com.server.pnd.domain.User;
import com.server.pnd.readme.dto.ReadmeDetailDto;
import com.server.pnd.readme.dto.ReadmeListSearchResponseDto;
import com.server.pnd.readme.dto.ReadmeSavedRequestDto;
import com.server.pnd.readme.dto.ReadmeSavedResponseDto;
import com.server.pnd.readme.repository.ReadmeRepository;
import com.server.pnd.repo.repository.RepoRepository;
import com.server.pnd.util.entity.BaseEntity;
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
public class ReadmeServiceImpl implements ReadmeService{
    private final JwtUtil jwtUtil;
    private final ReadmeRepository readmeRepository;
    private final RepoRepository repoRepository;

    // 마크다운 문서 저장
    @Override
    public ResponseEntity<CustomApiResponse<?>> savedReadme(ReadmeSavedRequestDto readmeSavedRequestDto) {
        Optional<Repo> foundRepo = repoRepository.findById(readmeSavedRequestDto.getRepoId());

        // Id에 해당하는 레포가 없는 경우 : 404
        if (foundRepo.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 레포 Id에 해당하는 레포가 DB에 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Repo repo = foundRepo.get();

        // 중복 조회
        String content = readmeSavedRequestDto.getContent();
        /*
        Optional<Readme> foundReadme = readmeRepository.findByTitleAndContent(title,content);

        // 이미 저장한 마크다운 파일인 경우 : 409
        if (foundReadme.isPresent()) {
            return ResponseEntity.status(409).body(CustomApiResponse.createFailWithoutData(409, "이미 DB에 저장된 마크다운 파일입니다."));
        }*/

        // DB에 저장
        Readme readme = Readme.builder()
                .readme_script(content)
                .repo(repo)
                .build();
        readmeRepository.save(readme);

        // data 가공
        ReadmeSavedResponseDto data = ReadmeSavedResponseDto.builder()
                .readmeId(readme.getId()).build();

        // 마크다운 저장 성공 : 201
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "리드미 파일 저장 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> searchReadmeList(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // 해당 회원의 마크다운 파일들 가져오기
        List<Readme> readmes = readmeRepository.findByUserId(user.getId());

        // 조회 성공 - 회원의 마크다운 파일이 존재하지 않는 경우 : 200
        if (readmes.isEmpty()) {
            return ResponseEntity.status(200).body(CustomApiResponse.createSuccess(200,null,"사용자의 마크다운 파일이 존재하지 않습니다."));
        }

        // data
        List<ReadmeListSearchResponseDto> responseDtos = new ArrayList<>();

        for (Readme readme : readmes) {
            ReadmeListSearchResponseDto responseDto = ReadmeListSearchResponseDto.builder()
                    .readmeId(readme.getId())
                    .content(readme.getReadme_script())
                    .title(readme.getTitle())
                    .build();
            responseDtos.add(responseDto);
        }

        // 조회 성공 - 회원의 마크다운 파일이 존재하는 경우 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, responseDtos, "마크다운 파일 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> searchReadme(Long readmeId) {
        Optional<Readme> foundReadme = readmeRepository.findById(readmeId);

        // 해당 ID의 마크다운 파일이 DB에 없는 경우 : 404
        if (foundReadme.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "해당 마크다운 파일이 DB에 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        Readme readme = foundReadme.get();

        // data
        ReadmeDetailDto data = ReadmeDetailDto.builder()
                .title(readme.getTitle())
                .content(readme.getReadme_script())
                .createdAt(readme.localDateTimeToString())
                .build();

        // 조회 성공 : 200
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "마크다운 파일 조회 완료되었습니다.");
        return ResponseEntity.status(200).body(res);
    }
}
