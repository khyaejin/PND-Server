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

        // 중복 조회
        String title = markdownSavedRequestDto.getTitle();
        String content = markdownSavedRequestDto.getContent();
        Optional<Markdown> foundMarkdown = markdownRepository.findByTitleAndContent(title,content);

        // 이미 저장한 마크다운 파일인 경우 : 409
        if (foundMarkdown.isPresent()) {
            return ResponseEntity.status(409).body(CustomApiResponse.createFailWithoutData(409, "이미 DB에 저장된 마크다운 파일입니다."));
        }

        // DB에 저장
        Markdown markdown = Markdown.builder()
                .title(title)
                .content(content)
                .build();
        markdownRepository.save(markdown);

        // data 가공
        MarkdownSavedResponseDto data = MarkdownSavedResponseDto.builder()
                .markdownId(markdown.getId()).build();

        // 마크다운 저장 성공 : 201
        CustomApiResponse<?> res = CustomApiResponse.createSuccess(201, data, "마크다운 파일 저장 완료되었습니다.");
        return ResponseEntity.status(201).body(res);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> searchMarkdownList(String authorizationHeader) {
        Optional<User> foundUser = jwtUtil.findUserByJwtToken(authorizationHeader);

        // 토큰에 해당하는 유저가 없는 경우 : 404
        if (foundUser.isEmpty()) {
            CustomApiResponse<?> res = CustomApiResponse.createFailWithoutData(404, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 사용자가 존재하지 않습니다.");
            return ResponseEntity.status(404).body(res);
        }
        User user = foundUser.get();
        return null;
    }
}
