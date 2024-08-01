package com.server.pnd.user.controller;

import com.server.pnd.domain.User;
import com.server.pnd.user.dto.TokenDto;
import com.server.pnd.user.dto.UserInfo;
import com.server.pnd.user.service.GithubSocialLoginServiceImpl;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pnd/user")
@RequiredArgsConstructor
public class SignController {
    private final GithubSocialLoginServiceImpl githubSocialLoginService;
    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    //깃허브 소셜 로그인
    @PostMapping(value = "/social/github")
    public ResponseEntity<CustomApiResponse<?>> githubLogin(@RequestParam String code) {
        // 1. 인가 코드 받기 (@RequestParam String code)
        logger.info("Request_Code: {}", code);
        if (code.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.createFailWithoutData(HttpStatus.FORBIDDEN.value(), "인가 코드를 전달받지 못했습니다."));
        }

        // 2. 접근 토큰 받기
        ResponseEntity<CustomApiResponse<?>> tokenResponse = githubSocialLoginService.getAccessToken(code);
        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
        }

        // 3. 사용자 정보 받기
        TokenDto tokenDto = (TokenDto) tokenResponse.getBody().getData(); // 접근토큰, 리프레시 토큰 받아오기

        ResponseEntity<CustomApiResponse<?>> userInfoResponse = githubSocialLoginService.getUserInfo(tokenDto);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
        }

        // 4. 레포지토리 정보 가져오기
        ResponseEntity<CustomApiResponse<?>> userRepositoryResponses = githubSocialLoginService.getUserRepository(tokenDto);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body(tokenResponse.getBody());
        }

        // 5. 로그인/회원가입 후 JWT 토큰 발급
        UserInfo userInfo = (UserInfo) userInfoResponse.getBody().getData(); //후에 서비스 계층 안으로 넣어주기
        //log를 통한 테스트 용도
        logger.info("User_Email: {}", userInfo.getEmail());
        logger.info("User_Name: {}", userInfo.getName());
        logger.info("User_Gtihub_id: {}", userInfo.getGithubId());
        logger.info("User_Image: {}", userInfo.getImage());
        logger.info("Uer_AccessToken: {}", userInfo.getAccessToken());
        ResponseEntity<CustomApiResponse<?>> loginResponse = githubSocialLoginService.login(userInfo);
        return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse.getBody());
    }
    /*@GetMapping(value = "/social/test/find/user")
    public Optional<User> testGetUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return jwtUtil.findUserByJwtToken(authorizationHeader);
    }

    @GetMapping(value = "/social/test/find/user/fail")
    public User testGetUserFail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        Optional<User> foundUser =  jwtUtil.findUserByJwtToken(authorizationHeader);
        if (foundUser.isEmpty()) {
            // 적절한 예외처리
        }
        User user = foundUser.get();
        return user;
    }*/
}
