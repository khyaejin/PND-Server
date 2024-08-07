package com.server.pnd.user.controller;

import com.server.pnd.user.service.UserService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // 레포지토리 전체 조회
    @GetMapping("/repository")
    public  ResponseEntity<CustomApiResponse<?>> getAllRepository(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.getAllRepository(authorizationHeader);
    }
}
