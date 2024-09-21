package com.server.pnd.user.service;

import com.server.pnd.user.dto.EditProfileRequestDto;
import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    // 프로필 조회
    ResponseEntity<CustomApiResponse<?>> getProfile(String authorizationHeader);

    //회원 탈퇴
    ResponseEntity<CustomApiResponse<?>> deleteUser(String authorizationHeader);

    // 프로필 편집
    ResponseEntity<CustomApiResponse<?>> editProfile(String authorizationHeader, EditProfileRequestDto editProfileRequestDto, MultipartFile images);
}
