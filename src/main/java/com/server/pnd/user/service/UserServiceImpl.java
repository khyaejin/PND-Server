package com.server.pnd.user.service;

import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    // 프로필 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> getProfile(String authorizationHeader) {

        return null;
    }
}
