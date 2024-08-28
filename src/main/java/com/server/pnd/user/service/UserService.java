package com.server.pnd.user.service;

import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    // 프로필 조회
    ResponseEntity<CustomApiResponse<?>> getProfile(String authorizationHeader);

    //회원 탈퇴
    ResponseEntity<CustomApiResponse<?>> deleteUser(String authorizationHeader);

    // 레포지토리 전체 조회
    ResponseEntity<CustomApiResponse<?>> getAllRepository(String authorizationHeader);

}
