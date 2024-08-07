package com.server.pnd.util.jwt;


import com.server.pnd.domain.User;
import com.server.pnd.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {
    // @Value의 값이 제대로 안 들어가는 오류가 생길 경우, 환경변수에서 jwt.secret 삭제
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    // 토큰으로 유저 찾기
    public Optional<User> findUserByJwtToken(String authorizationHeader) {
        String token = getTokenFromHeader(authorizationHeader);
        if (token == null) {
            log.warn("헤더에 JWT 토큰이 없음");
            return Optional.empty();
        }

        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            log.warn("유효하지 않은 JWT 토큰");
            return Optional.empty();
        }

        String githubId = getGithubIdPFromToken(token);

        if (githubId == null ) {
            log.warn("githubId가 JWT에 들어있지 않습니다.");
            return Optional.empty();
        }

        Optional<User> foundUser = userRepository.findByGithubId(githubId);
        if (foundUser.isEmpty()) {
            log.warn("해당 githubId를 가진 회원이 존재하지 않습니다.");
            return Optional.empty();
        }

        return foundUser;
    }

    private SecretKey getSigningKey() {
        log.info("secretKey : {}", secretKey);

        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final long EXPIRATION_TIME = 86400000; // 1일

    // 토큰 생성 (githubId 사용)
    public String createToken(String githubId) {
        return Jwts.builder()
                .setSubject(githubId)
                .claim("githubId", githubId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // SignatureAlgorithm을 명시적으로 추가
                .compact();
    }

    // 응답 헤더에서 토큰을 반환하는 메서드
    public String getTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        log.warn("Authorization 헤더가 유효하지 않습니다.");
        return null;
    }

    // 토큰에서 클레임을 추출하는 메서드 -> 토큰의 유효성 검사 (만료시간, 조작여부 등)
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 토큰입니다.");
            return null;
        }
    }

    // JWT 토큰에서 githubId 추출
    public String getGithubIdPFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.get("githubId", String.class);
        }
        return null;
    }

    // Jwt 토큰의 유효기간을 확인하는 메서드
    public Boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        }
        return null;
    }
}
