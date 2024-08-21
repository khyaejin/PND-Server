package com.server.pnd.report.controller;

import com.server.pnd.report.service.ReportService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd")
public class ReportController {

    final private ReportService reportService;
    @PostMapping("/report/{repoId}")
    public ResponseEntity<CustomApiResponse<?>> createReport(
            @RequestParam("repoID") Long repoId){
        return reportService.createReport(repoId);
    }

}
