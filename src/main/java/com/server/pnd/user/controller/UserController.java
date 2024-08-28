package com.server.pnd.user.controller;

import com.server.pnd.user.service.UserService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/pnd/user")
public class UserController {
    private final UserService userService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<CustomApiResponse<?>> getProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return userService.getProfile(authorizationHeader);
    }

    // 프로필 편집

    // 회원 탈퇴
    @DeleteMapping()
    public ResponseEntity<CustomApiResponse<?>> removeUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.deleteUser(authorizationHeader);
    }


}
