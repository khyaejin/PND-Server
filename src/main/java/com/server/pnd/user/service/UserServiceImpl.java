package com.server.pnd.user.service;

import com.server.pnd.domain.User;
import com.server.pnd.user.dto.SearchProfileResponseDto;
import com.server.pnd.user.repository.UserRepository;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
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
}
