package com.server.pnd.user.service;

import com.server.pnd.domain.Repository;
import com.server.pnd.domain.User;
import com.server.pnd.repository.repository.RepositoryRepository;
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
    private final RepositoryRepository repositoryRepository;
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

        // 프로필 조회 성공 (200)
        SearchProfileResponseDto data = SearchProfileResponseDto.builder()
                .name(user.getName())
                .image(user.getImage())
                .email(user.getEmail()).build();

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, data, "사용자 정보 조회 완료되었습니다.");
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

        List<Repository> repositories= repositoryRepository.findByUserId(user.getId());

        // 조회 성공 - 해당 회원의 깃허브 레포지토리가 존재하지 않는 경우 : 200
        if (repositories.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, null, "사용자의 레포지토리가 존재하지 않습니다.");
            return ResponseEntity.status(200).body(res);
        }

        // 조회 성공 - 해당 회원의 깃허브 레포지토리가 존재하는 경우 : 200
        List<SearchRepositoryResponseDto> responseDtos = new ArrayList<>();

        for (Repository repository : repositories) {
            SearchRepositoryResponseDto responseDto = SearchRepositoryResponseDto.builder()
                    .id(repository.getId())
                    .name(repository.getName())
                    .description(repository.getDescription())
                    .stars(repository.getStars())
                    .forksCount(repository.getForksCount())
                    .openIssues(repository.getOpenIssues())
                    .watchers(repository.getWatchers())
                    .language(repository.getLanguage())
                    .createdAt(repository.getFormattedCreatedAt()).build();
            responseDtos.add(responseDto);
        }
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, responseDtos, "레포지토리 전체 조회 성공했습니다.");
        return ResponseEntity.status(200).body(res);
    }
}
