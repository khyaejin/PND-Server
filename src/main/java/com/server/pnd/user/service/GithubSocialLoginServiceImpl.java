package com.server.pnd.user.service;

import com.server.pnd.domain.User;
import com.server.pnd.user.dto.SocialLoginResponseDto;
import com.server.pnd.user.dto.TokenDto;
import com.server.pnd.user.dto.UserInfo;
import com.server.pnd.user.jwt.JwtUtil;
import com.server.pnd.user.repository.UserRepository;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubSocialLoginServiceImpl implements SocialLoginService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(GithubSocialLoginServiceImpl.class);

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;
    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String githubRedirectUrl;
    @Value("${spring.security.oauth2.client.provider.github.token-uri}")
    private String githubReqUrl;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Override
    public ResponseEntity<CustomApiResponse<?>> getAccessToken(String code) {
        String accessToken;
        String refreshToken;
        TokenDto tokenDto;

        try {
            // 깃허브 인증을 위한 URL 설정
            String githubReqUrl = "https://github.com/login/oauth/access_token";
            URL url = new URL(githubReqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // HTTP POST 요청 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            // POST 파라미터 설정
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(githubClientId);
            sb.append("&client_secret=").append(githubClientSecret);
            sb.append("&redirect_uri=").append(githubRedirectUrl);
            sb.append("&code=").append(code);

            // 요청 파라미터 전송
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                bw.write(sb.toString());
                bw.flush();
            }

            // 접근토큰 받기
            int responseCode = conn.getResponseCode();
            logger.info("Token Response Code: {}", responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("Token_Response_Body: {}", result);


                // URL-encoded form 데이터 파싱
                Map<String, String> params = new HashMap<>();
                String[] pairs = result.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    params.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name()), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name()));
                }

                // 토큰과 관련 정보 추출
                if (params.containsKey("access_token")) {
                    
                    accessToken = params.get("access_token");
                    refreshToken = params.get("refresh_token"); // refresh_token 추출

                    tokenDto = TokenDto.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    
                    logger.info("Access Token: {}", accessToken);
                    logger.info("Refresh Token: {}", refreshToken); // refresh_token 로깅
                }else {
                    CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401,"이미 사용되었거나 유효하지 않은 인가 코드 입니다.");
                    return ResponseEntity.status(401).body(res);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401,"접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, tokenDto, "접근 토큰을 성공적으로 받았습니다.");
        return ResponseEntity.status(200).body(res);
    }

    // 회원 정보 받아오기
    @Override
    public ResponseEntity<CustomApiResponse<?>> getUserInfo(TokenDto tokenDto) {
        String githubId = null;
        String nickname = null;
        String email = null;
        String profileImageUrl = null;
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        String reqUrl = "https://api.github.com/user";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");  // GitHub API는 GET 메소드 사용
            conn.setRequestProperty("Authorization", "token " + accessToken);
            conn.setRequestProperty("Content-type", "application/json");  // GitHub API에서는 JSON 형식을 주로 사용

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            if (responseCode == 401) {
                CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401, "Access token is expired or invalid.");
                return ResponseEntity.status(401).body(res);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("User Info Response Body: {}", result);

                JSONObject jsonObject = new JSONObject(result);

                githubId = jsonObject.optString("id", null);
                nickname = jsonObject.optString("login", null); // GitHub uses 'login' as the username
                email = jsonObject.optString("email", null);
                profileImageUrl = jsonObject.optString("avatar_url", null); // GitHub uses 'avatar_url' for the profile image

                // Optionally handle additional fields like 'bio', 'followers' etc.
            }
        } catch (Exception e) {
            logger.error("Error getting user info", e);
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(400, "유저 정보를 가져오는데 실패했습니다.");
            return ResponseEntity.status(400).body(res);
        }

        UserInfo userInfo = UserInfo.builder()
                .githubId(githubId)
                .name(nickname)
                .email(email)
                .image(profileImageUrl)
                .accessToken(accessToken)
                .build();

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, userInfo, "유저 정보를 성공적으로 가져왔습니다.");
        return ResponseEntity.status(200).body(res);
    }


    @Override
    public ResponseEntity<CustomApiResponse<?>> login(UserInfo userInfo) {
        String githubId = userInfo.getGithubId();
        Optional<User> foundUser = userRepository.findByGithubId(githubId);

        String token = jwtUtil.createToken(githubId);
        SocialLoginResponseDto socialLoginResponseDto = SocialLoginResponseDto.builder()
                .token(token).build();

        if (foundUser.isEmpty()) {
            User user = userInfo.toEntity();
            userRepository.save(user);
            logger.info("User 회원가입 성공: {}", user.getName());
            CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, socialLoginResponseDto, "회원가입이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(201).body(res);
        } else {
            User user = foundUser.get();
            logger.info("User 로그인 성공: {}", user.getName());
            CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, socialLoginResponseDto, "로그인이 성공적으로 완료되었습니다.");
            return ResponseEntity.status(200).body(res);
        }
    }
}
