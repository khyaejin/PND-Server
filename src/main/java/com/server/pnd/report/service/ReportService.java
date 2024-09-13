package com.server.pnd.report.service;

import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ReportService {
    // report 생성
    ResponseEntity<CustomApiResponse<?>> createReport(Long repoId) throws IOException;

    // report 상세 조회
    ResponseEntity<CustomApiResponse<?>> searchDetail(Long repoId);
}
