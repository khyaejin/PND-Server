package com.server.pnd.report.service;

import com.server.pnd.util.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ReportService {
    // report 생성
    ResponseEntity<CustomApiResponse<?>> createReport(Long repoId);
}
