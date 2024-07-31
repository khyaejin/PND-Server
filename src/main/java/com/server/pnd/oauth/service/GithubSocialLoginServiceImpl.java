package com.server.pnd.oauth.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.server.pnd.domain.User;
import com.server.pnd.oauth.dto.SocialLoginResponseDto;
import com.server.pnd.oauth.dto.UserInfo;
import com.server.pnd.user.UserRepository;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
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
        try {
            URL url = new URL(githubReqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(githubClientId);
            sb.append("&client_secret=").append(githubClientSecret);
            sb.append("&redirect_uri=").append(githubRedirectUrl);
            sb.append("&code=").append(code);

            // 전송
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                bw.write(sb.toString());
                bw.flush();
            }

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
                logger.info("Token Response Body: {}", result);

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                } else {
                    CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401,"이미 사용되었거나 유효하지 않은 인가 코드 입니다.");
                    return ResponseEntity.status(401).body(res);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401,"접근 토큰을 받는데 실패했습니다.");
            return ResponseEntity.status(401).body(res);
        }

        CustomApiResponse<?> res = CustomApiResponse.createSuccess(200, accessToken, "접근 토큰을 성공적으로 받았습니다.");
        return ResponseEntity.status(200).body(res);
    }
/*
    @Override
    public ResponseEntity<CustomApiResponse<?>> getUserInfo(String accessToken) {
        String githubId = null;
        String nickname = null;
        String email = null;
        String profileImageUrl = null;

        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            int responseCode = conn.getResponseCode();
            logger.info("Response Code: {}", responseCode);

            if (responseCode == 401) {
                CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(401, "토큰이 만료되었거나 유효하지 않은 토큰입니다.");
                return ResponseEntity.status(401).body(res);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode <= 300 ? conn.getInputStream() : conn.getErrorStream()))) {
                String line;
                StringBuilder responseSb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseSb.append(line);
                }
                String result = responseSb.toString();
                logger.info("User Info Response Body: {}", result);

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("id")) {
                    githubId = String.valueOf(jsonObject.getLong("id"));
                } else {
                    logger.warn("No 'id' field in response");
                }

                if (jsonObject.has("properties")) {
                    nickname = jsonObject.getJSONObject("properties").optString("nickname", null);
                } else {
                    logger.warn("No 'properties' field in response");
                }

                if (jsonObject.has("kakao_account")) {
                    JSONObject kakaoAccount = jsonObject.getJSONObject("kakao_account");
                    email = kakaoAccount.optString("email", null);

                    if (kakaoAccount.has("profile")) {
                        JSONObject profile = kakaoAccount.getJSONObject("profile");
                        profileImageUrl = profile.optString("profile_image_url", null);
                    } else {
                        logger.warn("No 'profile' field in 'kakao_account'");
                    }
                } else {
                    logger.warn("No 'kakao_account' field in response");
                }
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
        Optional<User> foundUser = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId());

        String token = jwtUtil.createToken(userInfo.getProvider(), userInfo.getProviderId());
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
    }*/
}
