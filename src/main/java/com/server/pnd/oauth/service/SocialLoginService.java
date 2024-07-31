package com.server.pnd.oauth.service;


import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

//소셜로그인 공통 인터페이스
public interface SocialLoginService {
    //접근 토큰 받기
    ResponseEntity<CustomApiResponse<?>> getAccessToken(String code, String state);

    //사용자 정보 받기
    ResponseEntity<CustomApiResponse<?>> getUserInfo(String accessToken);

    //로그인/회원가입
    ResponseEntity<CustomApiResponse<?>> login(UserInfo userInfo);

}