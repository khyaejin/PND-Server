package com.server.pnd.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Commit {
    private String sha;
    private String message;
    private String author;
    private String url;
    // 추가적인 커밋 관련 필드가 필요하면 여기에 추가
}