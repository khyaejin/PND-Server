package com.server.pnd.markdown.service;

import com.server.pnd.domain.Markdown;
import com.server.pnd.domain.User;
import com.server.pnd.markdown.dto.MarkdownSavedRequestDto;
import com.server.pnd.markdown.dto.MarkdownSavedResponseDto;
import com.server.pnd.markdown.repository.MarkdownRepository;
import com.server.pnd.util.jwt.JwtUtil;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarkdownServiceImpl implements MarkdownService{
    private final JwtUtil jwtUtil;
    private final MarkdownRepository markdownRepository;

    // 마크다운 문서 저장
    @Override
    public ResponseEntity<CustomApiResponse<?>> savedMarkdown(String authorizationHeader, MarkdownSavedRequestDto markdownSavedRequestDto) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();

        // DB에 저장
        Markdown markdown = Markdown.builder()
                .title(markdownSavedRequestDto.getTitle())
                .content(markdownSavedRequestDto.getContent())
                .build();
        markdownRepository.save(markdown);

        // data 가공
        MarkdownSavedResponseDto data = MarkdownSavedResponseDto.builder()
                .markdownId(markdown.getId()).build();

        // 마크다운 저장 성공 : 201
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "마크다운 파일 저장 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }
}
